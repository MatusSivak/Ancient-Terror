package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Map;

/** Authenticated create-only REST client. Manual report callbacks use the render thread. */
public final class FirebaseBugReports {
    private final String collection;
    private final boolean renderCallbacks;

    public FirebaseBugReports() { this("bugReports", true); }

    private FirebaseBugReports(String collection, boolean renderCallbacks) {
        this.collection = collection;
        this.renderCallbacks = renderCallbacks;
    }

    public static FirebaseBugReports crashes() { return new FirebaseBugReports("crashReports", false); }
    public interface Callback {
        void success();
        void failed(String reason);
    }
    private String token;
    private String uid;
    private long tokenExpires;

    public void send(String id, String description, byte[] save, byte[] screenshot,
                     Map<String, String> metadata, long capturedAt, Callback callback) {
        try {
            JsonValue config = new JsonReader().parse(Gdx.files.internal("bug-report-firebase.json"));
            String project = config.getString("projectId");
            String key = config.getString("apiKey");
            if (project.isEmpty() || key.isEmpty()) { callback.failed("configuration"); return; }
            if (token != null && System.currentTimeMillis() < tokenExpires) {
                create(project, id, description, save, screenshot, metadata, capturedAt, callback);
                return;
            }
            Net.HttpRequest auth = request("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + key,
                    "{\"returnSecureToken\":true}");
            Gdx.net.sendHttpRequest(auth, new Listener(callback) {
                @Override void received(int status, String body) {
                    if (status != 200) { callback.failed("configuration"); return; }
                    JsonValue response = new JsonReader().parse(body);
                    token = response.getString("idToken");
                    uid = response.getString("localId");
                    tokenExpires = System.currentTimeMillis() + 50 * 60 * 1000;
                    create(project, id, description, save, screenshot, metadata, capturedAt, callback);
                }
            });
        } catch (Exception e) {
            callback.failed("configuration");
        }
    }

    private void create(String project, String id, String description, byte[] save, byte[] screenshot,
                        Map<String, String> metadata, long capturedAt, Callback callback) {
        String payload = BugReportPayload.document(description, save, screenshot, metadata, uid, capturedAt);
        Net.HttpRequest request = request("https://firestore.googleapis.com/v1/projects/" + project
                + "/databases/(default)/documents/" + collection + "?documentId=" + id, payload);
        request.setHeader("Authorization", "Bearer " + token);
        Gdx.net.sendHttpRequest(request, new Listener(callback) {
            @Override void received(int status, String body) {
                // The same ID and frozen payload are reused after a lost response.
                if (status >= 200 && status < 300 || status == 409) callback.success();
                else {
                    if (status == 401) { token = null; tokenExpires = 0; }
                    callback.failed(status == 401 || status == 403 || status == 404 ? "configuration" : "network");
                }
            }
        });
    }

    private static Net.HttpRequest request(String url, String content) {
        Net.HttpRequest request = new Net.HttpRequest("POST");
        request.setUrl(url);
        request.setHeader("Content-Type", "application/json; charset=UTF-8");
        request.setTimeOut(20000);
        request.setContent(content);
        return request;
    }

    private void dispatch(Runnable runnable) {
        if (renderCallbacks) Gdx.app.postRunnable(runnable);
        else runnable.run();
    }

    private abstract class Listener implements Net.HttpResponseListener {
        final Callback callback;
        Listener(Callback callback) { this.callback = callback; }
        abstract void received(int status, String body);
        @Override public void handleHttpResponse(Net.HttpResponse response) {
            int status = response.getStatus().getStatusCode();
            String body = response.getResultAsString();
            dispatch(() -> {
                try { received(status, body); }
                catch (Exception e) { callback.failed("network"); }
            });
        }
        @Override public void failed(Throwable error) { dispatch(() -> callback.failed("network")); }
        @Override public void cancelled() { dispatch(() -> callback.failed("network")); }
    }
}
