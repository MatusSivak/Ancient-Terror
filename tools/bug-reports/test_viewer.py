import base64
import json
import io
import threading
import unittest
from http.server import ThreadingHTTPServer
from unittest.mock import patch
from urllib.error import HTTPError
from urllib.request import Request, urlopen
from urllib.parse import parse_qs, urlsplit

from viewer import Firestore, STATUSES, decode, handler_for, summarize

SAVE = b'{"exact": "original"}\r\n'
PNG = base64.b64decode('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a4WQAAAAASUVORK5CYII=')
DOCUMENT = {'name': 'projects/test/databases/(default)/documents/bugReports/test-id',
            'createTime': '2026-09-26T12:00:00Z',
            'updateTime': '2026-09-26T12:00:00Z',
            'fields': {'description': {'stringValue': '<script>alert(1)</script>\nA problem'},
                       'metadata': {'mapValue': {'fields': {'platform': {'stringValue': 'Desktop'}}}},
                       'saveFile': {'bytesValue': base64.b64encode(SAVE).decode()},
                       'screenshotPng': {'bytesValue': base64.b64encode(PNG).decode()},
                       'unknownField': {'integerValue': '9007199254740993'}}}


class FakeStore:
    def set_status(self, report_id, status, update_time):
        return {**DOCUMENT, 'fields': {**DOCUMENT['fields'], 'status': {'stringValue': status}}}

    def delete(self, report_id, update_time):
        pass

    def list(self, token):
        return {'reports': [summarize(DOCUMENT)], 'nextPageToken': ''}

    def report(self, report_id):
        return DOCUMENT


class ViewerTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.server = ThreadingHTTPServer(('127.0.0.1', 0), handler_for(FakeStore(), 'test-session', 'test'))
        cls.thread = threading.Thread(target=cls.server.serve_forever, daemon=True)
        cls.thread.start()
        cls.url = f'http://127.0.0.1:{cls.server.server_port}'

    @classmethod
    def tearDownClass(cls):
        cls.server.shutdown()
        cls.server.server_close()
        cls.thread.join()

    def request(self, path, headers=None, method='GET', body=None):
        headers = headers if headers is not None else {'X-Viewer-Session': 'test-session'}
        if body is not None:
            headers = {**headers, 'Content-Type': 'application/json'}
        return urlopen(Request(self.url + path, headers=headers, method=method,
                              data=json.dumps(body).encode() if body is not None else None))

    def test_status_update_and_confirmed_delete(self):
        for status in STATUSES:
            with self.request('/api/report?id=test-id', method='PATCH', body={'status': status, 'updateTime': DOCUMENT['updateTime']}) as response:
                result = json.load(response)
            self.assertEqual(result['fields']['status'], status)
            self.assertEqual(result['fields']['saveFile']['size'], len(SAVE))
        with patch.object(FakeStore, 'delete') as delete:
            with self.request('/api/report?id=test-id', method='DELETE', body={'confirmId': 'test-id', 'updateTime': DOCUMENT['updateTime']}) as response:
                self.assertEqual(json.load(response), {'deleted': 'test-id'})
            delete.assert_called_once_with('test-id', DOCUMENT['updateTime'])

    def test_invalid_mutations_never_reach_firestore(self):
        cases = [('PATCH', {'status': 'invalid', 'updateTime': 'time'}),
                 ('PATCH', {'status': 'fixed'}),
                 ('PATCH', {'status': [], 'updateTime': 'time'}),
                 ('DELETE', {'confirmId': 'wrong-id', 'updateTime': 'time'})]
        with patch.object(FakeStore, 'set_status') as update, patch.object(FakeStore, 'delete') as delete:
            for method, body in cases:
                with self.assertRaises(HTTPError) as context:
                    self.request('/api/report?id=test-id', method=method, body=body)
                self.assertEqual(context.exception.code, 400)
                context.exception.close()
            update.assert_not_called()
            delete.assert_not_called()

    def test_mutations_require_local_session(self):
        for method in ['PATCH', 'DELETE']:
            for headers in [{}, {'X-Viewer-Session': 'wrong'},
                            {'X-Viewer-Session': 'test-session', 'Origin': 'https://evil.example'},
                            {'X-Viewer-Session': 'test-session', 'Host': 'evil.example'}]:
                with self.assertRaises(HTTPError) as context:
                    self.request('/api/report?id=test-id', headers=headers, method=method,
                                 body={'status': 'fixed', 'confirmId': 'test-id', 'updateTime': 'time'})
                self.assertEqual(context.exception.code, 403)
                context.exception.close()

    def test_firestore_write_mask_precondition_and_empty_delete_response(self):
        client = Firestore('test', '(default)')
        with patch.object(client, 'token', return_value='test-token'), patch('viewer.urlopen') as fetch:
            fetch.return_value.__enter__.return_value = io.BytesIO(json.dumps(DOCUMENT).encode())
            client.set_status('test-id', 'fixed', DOCUMENT['updateTime'])
            request = fetch.call_args.args[0]
            self.assertEqual(request.method, 'PATCH')
            self.assertEqual(json.loads(request.data), {'fields': {'status': {'stringValue': 'fixed'}}})
            self.assertEqual(parse_qs(urlsplit(request.full_url).query),
                             {'updateMask.fieldPaths': ['status'], 'currentDocument.updateTime': [DOCUMENT['updateTime']]})
            fetch.return_value.__enter__.return_value = io.BytesIO(b'')
            client.delete('test-id', DOCUMENT['updateTime'])
            request = fetch.call_args.args[0]
            self.assertEqual(request.method, 'DELETE')
            self.assertIsNone(request.data)
            self.assertEqual(parse_qs(urlsplit(request.full_url).query), {'currentDocument.updateTime': [DOCUMENT['updateTime']]})

    def test_stale_version_and_permission_failures_are_actionable(self):
        client = Firestore('test', '(default)')
        for code, message in [(403, 'permission'), (409, 'Refresh'), (412, 'Refresh')]:
            with patch.object(client, 'token', return_value='test-token'), patch('viewer.urlopen', side_effect=HTTPError('url', code, '', {}, None)):
                with self.assertRaisesRegex(RuntimeError, message):
                    client.set_status('test-id', 'fixed', DOCUMENT['updateTime'])
        conflict = HTTPError('url', 400, '', {}, io.BytesIO(b'{"error":{"status":"FAILED_PRECONDITION"}}'))
        with patch.object(client, 'token', return_value='test-token'), patch('viewer.urlopen', side_effect=conflict):
            with self.assertRaisesRegex(RuntimeError, 'Refresh'):
                client.delete('test-id', DOCUMENT['updateTime'])

    def test_attachment_bytes_are_exact(self):
        for kind, expected in [('saveFile', SAVE), ('screenshotPng', PNG)]:
            with self.request('/api/report?id=test-id&attachment=' + kind) as response:
                self.assertEqual(response.read(), expected)
                self.assertEqual(response.headers['Cache-Control'], 'no-store')

    def test_all_fields_and_original_export(self):
        with self.request('/api/report?id=test-id') as response:
            report = json.load(response)
        self.assertEqual(report['fields']['unknownField'], '9007199254740993')
        self.assertEqual(report['fields']['screenshotPng']['size'], len(PNG))
        with self.request('/api/report?id=test-id&attachment=raw') as response:
            self.assertEqual(json.load(response), DOCUMENT)

    def test_local_api_requires_session_and_host(self):
        for headers in [{}, {'X-Viewer-Session': 'wrong'}, {'X-Viewer-Session': 'test-session', 'Host': 'evil.example'}]:
            with self.assertRaises(HTTPError) as context:
                self.request('/api/reports', headers)
            self.assertEqual(context.exception.code, 403)

    def test_bad_ids_and_attachment_types(self):
        for path in ['/api/report?id=..', '/api/report?id=a%2Fb', '/api/report?id=test&attachment=other']:
            with self.assertRaises(HTTPError) as context:
                self.request(path)
            self.assertEqual(context.exception.code, 400)

    def test_nested_firestore_values(self):
        self.assertEqual(decode({'arrayValue': {'values': [{'nullValue': None}, {'booleanValue': False}, {'mapValue': {}}]}}), [None, False, {}])

    def test_pagination_and_attachment_mask(self):
        client = Firestore('test', '(default)')
        with patch.object(client, 'get', return_value={'documents': [DOCUMENT], 'nextPageToken': 'next'}) as get:
            result = client.list('previous')
        params = get.call_args.kwargs['params']
        self.assertEqual(params['pageToken'], 'previous')
        self.assertNotIn('screenshotPng', params['mask.fieldPaths'])
        self.assertEqual(result['nextPageToken'], 'next')

    def test_static_files_and_error_response(self):
        for path in ['/', '/app.js', '/style.css']:
            with self.request(path, {}) as response:
                self.assertEqual(response.status, 200)
        with patch.object(FakeStore, 'list', side_effect=RuntimeError('Access denied')):
            with self.assertRaises(HTTPError) as context:
                self.request('/api/reports')
            self.assertEqual(json.load(context.exception)['error'], 'Access denied')


if __name__ == '__main__':
    unittest.main()
