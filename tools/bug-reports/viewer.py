"""Local Firestore bug report manager. Run with --help for options."""
import argparse
import base64
import json
import os
from pathlib import Path
import secrets
import threading
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.error import HTTPError
from urllib.parse import parse_qs, quote, urlencode, urlsplit
from urllib.request import Request, urlopen
import webbrowser

ROOT = Path(__file__).resolve().parent
PROJECT = "ancient-terror-hall-of-fame"
STATUSES = {"new": "New", "investigating": "Investigating", "fixed": "Fixed", "closed": "Closed"}


def decode(value):
    """Preserve integer precision and expose every Firestore value type."""
    if "mapValue" in value:
        return {k: decode(v) for k, v in value["mapValue"].get("fields", {}).items()}
    if "arrayValue" in value:
        return [decode(v) for v in value["arrayValue"].get("values", [])]
    if "bytesValue" in value:
        return {"type": "bytes", "size": len(base64.b64decode(value["bytesValue"]))}
    return next(iter(value.values()), None)


def summarize(document):
    fields = document.get("fields", {})
    return {"id": document["name"].rsplit("/", 1)[-1],
            "name": document["name"], "createTime": document.get("createTime"),
            "updateTime": document.get("updateTime"),
            "fields": {k: decode(v) for k, v in fields.items()}}


class Firestore:
    def __init__(self, project, database):
        self.base = ("https://firestore.googleapis.com/v1/projects/" + quote(project, safe="")
                     + "/databases/" + quote(database, safe="") + "/documents/bugReports")
        self.credentials = None
        self.lock = threading.Lock()

    def token(self):
        token = os.environ.get("GOOGLE_OAUTH_ACCESS_TOKEN")
        if token:
            return token
        try:
            import google.auth
            from google.auth.transport.requests import Request as AuthRequest
        except ImportError:
            raise RuntimeError("Install requirements.txt, then run gcloud auth application-default login, "
                               "or set GOOGLE_APPLICATION_CREDENTIALS to a service account JSON file.")
        with self.lock:
            try:
                if self.credentials is None:
                    self.credentials, _ = google.auth.default(
                        scopes=["https://www.googleapis.com/auth/datastore"])
                if not self.credentials.valid:
                    self.credentials.refresh(AuthRequest())
                return self.credentials.token
            except Exception:
                raise RuntimeError("Google credentials unavailable. Run gcloud auth application-default login "
                                   "or configure GOOGLE_APPLICATION_CREDENTIALS. The account needs Firestore read access.") from None

    def get(self, suffix="", params=None):
        return self.request("GET", suffix, params)

    def request(self, method, suffix="", params=None, body=None):
        url = self.base + suffix + ("?" + urlencode(params, doseq=True) if params else "")
        request = Request(url, method=method,
                          data=json.dumps(body).encode() if body is not None else None,
                          headers={"Authorization": "Bearer " + self.token(), "Content-Type": "application/json"})
        try:
            with urlopen(request, timeout=30) as response:
                data = response.read()
                return json.loads(data) if data else {}
        except HTTPError as error:
            try:
                error_status = json.loads(error.read()).get("error", {}).get("status")
            except (ValueError, AttributeError):
                error_status = None
            if error_status == "FAILED_PRECONDITION":
                raise RuntimeError("This report changed since you opened it. Refresh and try again.") from None
            messages = {401: "Google authentication expired or was rejected. Refresh your credentials.",
                        403: "Access denied. Reading requires Firestore read permission; status changes and deletion require update/delete permission (for example roles/datastore.user).",
                        404: "Database or report not found. Refresh the reports and check the project and database options.",
                        409: "This report changed since you opened it. Refresh and try again.",
                        412: "This report changed since you opened it. Refresh and try again."}
            raise RuntimeError(messages.get(error.code, f"Firestore returned HTTP {error.code}. Try again.")) from None

    def list(self, page_token):
        params = {"pageSize": 100, "mask.fieldPaths": ["description", "status", "capturedAt", "metadata", "reporterUid", "schemaVersion"]}
        if page_token:
            params["pageToken"] = page_token
        result = self.get(params=params)
        return {"reports": [summarize(d) for d in result.get("documents", [])],
                "nextPageToken": result.get("nextPageToken", "")}

    def report(self, report_id):
        return self.get("/" + quote(report_id, safe=""))

    def set_status(self, report_id, status, update_time):
        if status not in STATUSES:
            raise ValueError("Unknown status")
        return self.request("PATCH", "/" + quote(report_id, safe=""),
                            {"updateMask.fieldPaths": "status", "currentDocument.updateTime": update_time},
                            {"fields": {"status": {"stringValue": status}}})

    def delete(self, report_id, update_time):
        self.request("DELETE", "/" + quote(report_id, safe=""),
                     {"currentDocument.updateTime": update_time})


def handler_for(store, session, project):
    class Handler(BaseHTTPRequestHandler):
        def log_message(self, *_):
            pass  # Do not log report paths or the local session token.

        def send(self, status, body, content_type="application/json", filename=None):
            if isinstance(body, (dict, list)):
                body = json.dumps(body).encode()
            self.send_response(status)
            self.send_header("Content-Type", content_type)
            self.send_header("Content-Length", str(len(body)))
            self.send_header("Cache-Control", "no-store")
            self.send_header("X-Content-Type-Options", "nosniff")
            self.send_header("Referrer-Policy", "no-referrer")
            self.send_header("Content-Security-Policy", "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' blob:; object-src 'none'; frame-ancestors 'none'")
            if filename:
                self.send_header("Content-Disposition", f'attachment; filename="{filename}"')
            self.end_headers()
            self.wfile.write(body)

        def do_GET(self):
            if self.headers.get("Host") != f"127.0.0.1:{self.server.server_port}":
                self.send(403, {"error": "Invalid host"})
                return
            url = urlsplit(self.path)
            if url.path in ("/", "/app.js", "/style.css"):
                name, mime = {"/": ("index.html", "text/html; charset=utf-8"),
                              "/app.js": ("app.js", "text/javascript"),
                              "/style.css": ("style.css", "text/css")}[url.path]
                self.send(200, (ROOT / name).read_bytes(), mime)
                return
            if not secrets.compare_digest(self.headers.get("X-Viewer-Session", ""), session):
                self.send(403, {"error": "Open the viewer using the full URL printed by viewer.py."})
                return
            try:
                query = parse_qs(url.query)
                if url.path == "/api/reports":
                    self.send(200, {**store.list(query.get("pageToken", [""])[0]), "project": project, "statuses": STATUSES})
                elif url.path == "/api/report":
                    report_id = query.get("id", [""])[0]
                    if not report_id or "/" in report_id or report_id in (".", ".."):
                        self.send(400, {"error": "Invalid report ID"})
                        return
                    document = store.report(report_id)
                    attachment = query.get("attachment", [""])[0]
                    if attachment:
                        if attachment not in ("screenshotPng", "saveFile", "raw"):
                            self.send(400, {"error": "Unknown attachment"})
                        elif attachment == "raw":
                            self.send(200, document, filename="report.json")
                        else:
                            value = document.get("fields", {}).get(attachment, {}).get("bytesValue")
                            if value is None:
                                self.send(404, {"error": "Attachment is missing"})
                            else:
                                screenshot = attachment == "screenshotPng"
                                self.send(200, base64.b64decode(value, validate=True),
                                          "image/png" if screenshot else "application/octet-stream",
                                          "screenshot.png" if screenshot else "save.json")
                    else:
                        self.send(200, summarize(document))
                else:
                    self.send(404, {"error": "Not found"})
            except RuntimeError as error:
                self.send(502, {"error": str(error)})
            except Exception:
                self.send(502, {"error": "Could not load the report. Check your network and try again."})

        def do_PATCH(self):
            self.mutate()

        def do_DELETE(self):
            self.mutate()

        def mutate(self):
            host = f"127.0.0.1:{self.server.server_port}"
            if (self.headers.get("Host") != host
                    or self.headers.get("Origin", "http://" + host) != "http://" + host
                    or not secrets.compare_digest(self.headers.get("X-Viewer-Session", ""), session)):
                self.send(403, {"error": "Invalid viewer session. Reopen the URL printed by viewer.py."})
                return
            url = urlsplit(self.path)
            if url.path != "/api/report":
                self.send(404, {"error": "Not found"})
                return
            try:
                report_id = parse_qs(url.query).get("id", [""])[0]
                if not report_id or "/" in report_id or report_id in (".", ".."):
                    raise ValueError("Invalid report ID")
                length = int(self.headers.get("Content-Length", "0"))
                if not 0 < length <= 4096 or self.headers.get_content_type() != "application/json":
                    raise ValueError("A JSON request body is required (maximum 4096 bytes)")
                body = json.loads(self.rfile.read(length))
                if not isinstance(body, dict):
                    raise ValueError("Expected a JSON object")
                update_time = body.get("updateTime")
                if not isinstance(update_time, str) or not update_time.strip():
                    raise ValueError("Refresh this report before changing it")
                if self.command == "PATCH":
                    status = body.get("status")
                    if not isinstance(status, str) or status not in STATUSES:
                        raise ValueError("Unknown status")
                    self.send(200, summarize(store.set_status(report_id, status, update_time)))
                else:
                    if body.get("confirmId") != report_id:
                        raise ValueError("Deletion confirmation does not match the report ID")
                    store.delete(report_id, update_time)
                    self.send(200, {"deleted": report_id})
            except (ValueError, UnicodeDecodeError) as error:
                self.send(400, {"error": str(error)})
            except RuntimeError as error:
                self.send(502, {"error": str(error)})
            except Exception:
                self.send(502, {"error": "The change could not be confirmed. Refresh reports before retrying."})
    return Handler


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--project", default=PROJECT)
    parser.add_argument("--database", default="(default)")
    parser.add_argument("--port", type=int, default=8765)
    parser.add_argument("--no-browser", action="store_true")
    args = parser.parse_args()
    session = secrets.token_urlsafe(32)
    server = ThreadingHTTPServer(("127.0.0.1", args.port), handler_for(Firestore(args.project, args.database), session, args.project))
    url = f"http://127.0.0.1:{server.server_port}/#session={session}"
    print(f"Bug report viewer: {url}\nPress Ctrl+C to stop.", flush=True)
    if not args.no_browser:
        webbrowser.open(url)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        pass
    finally:
        server.server_close()


if __name__ == "__main__":
    main()
