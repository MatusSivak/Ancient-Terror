import base64
import json
import io
import re
import threading
import unittest
from http.server import ThreadingHTTPServer
from unittest.mock import patch
from urllib.error import HTTPError
from urllib.request import Request, urlopen
from urllib.parse import parse_qs, urlsplit

from viewer import Firestore, STATUSES, ViewerHTTPServer, decode, handler_for, summarize

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


CRASH = {**DOCUMENT, 'name': DOCUMENT['name'].replace('/bugReports/', '/crashReports/'),
         'fields': {**DOCUMENT['fields'], 'description': {'stringValue': 'TEST crash\nStack trace'},
                    'saveFile': {'bytesValue': base64.b64encode(b'crash save').decode()}}}


class FakeStore:
    def set_title(self, report_id, title, update_time, collection="bugReports"):
        document = self.report(report_id, collection)
        return {**document, 'fields': {**document['fields'], 'title': {'stringValue': title}}}

    def set_status(self, report_id, status, update_time, collection="bugReports"):
        document = self.report(report_id, collection)
        return {**document, 'fields': {**document['fields'], 'status': {'stringValue': status}}}

    def delete(self, report_id, update_time, collection="bugReports"):
        pass

    def list(self, token, collection="bugReports"):
        return {'reports': [summarize(self.report('test-id', collection))], 'nextPageToken': ''}

    def report(self, report_id, collection="bugReports"):
        return CRASH if collection == "crashReports" else DOCUMENT


class ViewerTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.server = ViewerHTTPServer(('127.0.0.1', 0), handler_for(FakeStore(), 'test-session', 'test'))
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
            delete.assert_called_once_with('test-id', DOCUMENT['updateTime'], 'bugReports')

    def test_both_collections_with_same_id_remain_distinct(self):
        for collection, document, save in [('bugReports', DOCUMENT, SAVE), ('crashReports', CRASH, b'crash save')]:
            with self.subTest(collection=collection):
                with self.request('/api/reports?collection=' + collection) as response:
                    reports = json.load(response)['reports']
                self.assertEqual(reports[0]['collection'], collection)
                self.assertEqual(reports[0]['id'], 'test-id')
                path = '/api/report?id=test-id&collection=' + collection
                with self.request(path) as response:
                    self.assertEqual(json.load(response)['name'], document['name'])
                for attachment, expected in [('saveFile', save), ('screenshotPng', PNG)]:
                    with self.request(path + '&attachment=' + attachment) as response:
                        self.assertEqual(response.read(), expected)
                with self.request(path + '&attachment=raw') as response:
                    self.assertEqual(json.load(response), document)
                with self.request(path, method='PATCH', body={'status': 'fixed', 'updateTime': document['updateTime']}) as response:
                    updated = json.load(response)
                self.assertEqual(updated['collection'], collection)
                self.assertEqual(updated['fields']['status'], 'fixed')
                with patch.object(FakeStore, 'delete') as delete:
                    self.request(path, method='DELETE', body={'confirmId': 'test-id', 'updateTime': document['updateTime']}).close()
                    delete.assert_called_once_with('test-id', document['updateTime'], collection)

    def test_title_update_preserves_original_report(self):
        for collection, document in [('bugReports', DOCUMENT), ('crashReports', CRASH)]:
            for title in ['  Clearer title <script>  ', '', 'x' * 200]:
                with self.request('/api/report?id=test-id&collection=' + collection, method='PATCH',
                                  body={'title': title, 'updateTime': document['updateTime']}) as response:
                    result = json.load(response)
                self.assertEqual(result['collection'], collection)
                self.assertEqual(result['fields'].pop('title'), title.strip())
                self.assertEqual(result['fields'], summarize(document)['fields'])

    def test_invalid_titles_never_reach_store(self):
        cases = [{'title': title, 'updateTime': 'time'} for title in
                 [None, [], 123, 'x' * 201, 'two\nlines', 'tab\there']]
        cases += [{'title': 'Missing version'}, {'title': 'Both', 'status': 'fixed', 'updateTime': 'time'},
                  {'title': 'Extra', 'description': 'overwrite', 'updateTime': 'time'}]
        with patch.object(FakeStore, 'set_title') as update:
            for body in cases:
                with self.assertRaises(HTTPError) as error:
                    self.request('/api/report?id=test-id', method='PATCH', body=body)
                self.assertEqual(error.exception.code, 400)
                error.exception.close()
            update.assert_not_called()

    def test_title_write_mask_and_version_precondition(self):
        client = Firestore('test', '(default)')
        for collection in ['bugReports', 'crashReports']:
            with patch.object(client, 'request', return_value=DOCUMENT) as request:
                client.set_title('test-id', ' Renamed report ', DOCUMENT['updateTime'], collection)
                request.assert_called_once_with('PATCH', '/test-id',
                    {'updateMask.fieldPaths': 'title', 'currentDocument.updateTime': DOCUMENT['updateTime']},
                    {'fields': {'title': {'stringValue': 'Renamed report'}}}, collection=collection)

    def test_invalid_collections_never_reach_store(self):
        with patch.object(FakeStore, 'list') as listing, patch.object(FakeStore, 'report') as report, \
                patch.object(FakeStore, 'set_status') as update, patch.object(FakeStore, 'delete') as delete:
            for collection in ['other', '../crashReports', 'bugReports/test-id']:
                for path, method in [('/api/reports', 'GET'), ('/api/report?id=test-id', 'GET'),
                                     ('/api/report?id=test-id', 'PATCH'), ('/api/report?id=test-id', 'DELETE')]:
                    url = path + ('&' if '?' in path else '?') + 'collection=' + collection
                    with self.assertRaises(HTTPError) as error:
                        self.request(url, method=method, body=None if method == 'GET' else {
                            'status': 'fixed', 'confirmId': 'test-id', 'updateTime': DOCUMENT['updateTime']})
                    self.assertEqual(error.exception.code, 400)
                    error.exception.close()
            for operation in [listing, report, update, delete]:
                operation.assert_not_called()

    def test_firestore_routes_every_operation_to_selected_collection(self):
        client = Firestore('test', '(default)')
        for collection in ['bugReports', 'crashReports']:
            with patch.object(client, 'token', return_value='token'), patch('viewer.urlopen') as fetch:
                def response(value):
                    fetch.return_value.__enter__.return_value = io.BytesIO(json.dumps(value).encode())
                response({'documents': [], 'nextPageToken': 'next'})
                self.assertEqual(client.list('previous', collection)['nextPageToken'], 'next')
                url = urlsplit(fetch.call_args.args[0].full_url)
                self.assertTrue(url.path.endswith('/documents/' + collection))
                self.assertEqual(parse_qs(url.query)['pageToken'], ['previous'])
                for operation in [lambda: client.report('test-id', collection),
                                  lambda: client.set_status('test-id', 'fixed', DOCUMENT['updateTime'], collection),
                                  lambda: client.delete('test-id', DOCUMENT['updateTime'], collection)]:
                    response(DOCUMENT)
                    operation()
                    self.assertTrue(urlsplit(fetch.call_args.args[0].full_url).path.endswith('/documents/' + collection + '/test-id'))
        with self.assertRaises(ValueError):
            client.report('test-id', 'other')

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

    def test_plain_page_bootstraps_current_session_for_both_collections(self):
        with self.request('/', {}) as response:
            html = response.read().decode()
            self.assertEqual(response.headers['Cache-Control'], 'no-store')
            self.assertIn("frame-ancestors 'none'", response.headers['Content-Security-Policy'])
            self.assertIsNone(response.headers.get('Access-Control-Allow-Origin'))
        session = re.search(r'name="viewer-session" content="([^"]+)"', html).group(1)
        self.assertEqual(session, 'test-session')
        self.assertNotIn('__VIEWER_SESSION__', html)
        for collection in ['bugReports', 'crashReports']:
            with self.request('/api/reports?collection=' + collection,
                              {'X-Viewer-Session': session}) as response:
                self.assertEqual(json.load(response)['reports'][0]['collection'], collection)

    def test_second_viewer_cannot_share_running_viewers_port(self):
        with self.assertRaises(OSError):
            second = ViewerHTTPServer(self.server.server_address,
                                      handler_for(FakeStore(), 'other-session', 'test'))
            second.server_close()
        with self.request('/api/reports') as response:
            self.assertEqual(response.status, 200)

    def test_reloading_after_server_restart_gets_new_session(self):
        original_handler = self.server.RequestHandlerClass
        self.server.RequestHandlerClass = handler_for(FakeStore(), 'restarted-session', 'test')
        try:
            with self.assertRaises(HTTPError) as error:
                self.request('/api/reports')
            self.assertEqual(error.exception.code, 403)
            self.assertIn('Reload this page', json.load(error.exception)['error'])
            with self.request('/', {}) as response:
                html = response.read().decode()
            session = re.search(r'name="viewer-session" content="([^"]+)"', html).group(1)
            self.assertEqual(session, 'restarted-session')
            with self.request('/api/reports', {'X-Viewer-Session': session}) as response:
                self.assertEqual(response.status, 200)
        finally:
            self.server.RequestHandlerClass = original_handler

    def test_bootstrap_rejects_untrusted_host_without_leaking_session(self):
        with self.assertRaises(HTTPError) as error:
            self.request('/', {'Host': 'evil.example'})
        self.assertEqual(error.exception.code, 403)
        self.assertNotIn(b'test-session', error.exception.read())

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
        self.assertIn('title', params['mask.fieldPaths'])
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
