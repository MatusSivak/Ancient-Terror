package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

public class FirebaseBugReportsTest {
    private Application oldApp;
    private Files oldFiles;
    private Net oldNet;
    private final List<Net.HttpRequest> requests = new ArrayList<>();
    private int resultStatus = 200;
    private boolean offline;
    private int successes;
    private String failure;
    private final FirebaseBugReports.Callback callback = new FirebaseBugReports.Callback() {
        public void success() { successes++; }
        public void failed(String reason) { failure = reason; }
    };

    @Before public void setup() {
        oldApp = Gdx.app; oldFiles = Gdx.files; oldNet = Gdx.net;
        Gdx.app = (Application) Proxy.newProxyInstance(Application.class.getClassLoader(), new Class[]{Application.class},
                (proxy, method, args) -> { if (method.getName().equals("postRunnable")) ((Runnable) args[0]).run(); return null; });
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class[]{Files.class},
                (proxy, method, args) -> new FileHandle("config") {
                    @Override public InputStream read() {
                        return new ByteArrayInputStream("{\"projectId\":\"test-project\",\"apiKey\":\"test-key\"}".getBytes(StandardCharsets.UTF_8));
                    }
                });
        Gdx.net = (Net) Proxy.newProxyInstance(Net.class.getClassLoader(), new Class[]{Net.class}, (proxy, method, args) -> {
            if (!method.getName().equals("sendHttpRequest")) return null;
            Net.HttpRequest request = (Net.HttpRequest) args[0];
            requests.add(request);
            Net.HttpResponseListener listener = (Net.HttpResponseListener) args[1];
            if (offline) { listener.failed(new RuntimeException("offline")); return null; }
            boolean auth = request.getUrl().contains("accounts:signUp");
            Net.HttpResponse response = (Net.HttpResponse) Proxy.newProxyInstance(Net.HttpResponse.class.getClassLoader(),
                    new Class[]{Net.HttpResponse.class}, (p, m, a) -> {
                        if (m.getName().equals("getStatus")) return new HttpStatus(auth ? 200 : resultStatus);
                        if (m.getName().equals("getResultAsString")) return auth ? "{\"idToken\":\"test-token\",\"localId\":\"test-user\"}" : "{}";
                        return null;
                    });
            listener.handleHttpResponse(response);
            return null;
        });
    }

    @After public void restore() { Gdx.app = oldApp; Gdx.files = oldFiles; Gdx.net = oldNet; }

    private void send() {
        new FirebaseBugReports().send("stable-report-id", "Bug", null, null, Collections.emptyMap(), 123, callback);
    }

    @Test public void postsAuthenticatedDocumentAndRetriesWithSameId() {
        send();
        Net.HttpRequest request = requests.get(requests.size() - 1);
        assertTrue(request.getUrl().endsWith("/documents/bugReports?documentId=stable-report-id"));
        assertEquals("Bearer test-token", request.getHeaders().get("Authorization"));
        assertEquals(1, successes);
        resultStatus = 409;
        send();
        assertEquals(request.getUrl(), requests.get(requests.size() - 1).getUrl());
        assertEquals(request.getContent(), requests.get(requests.size() - 1).getContent());
        assertEquals(2, successes);
    }

    @Test public void deniedWriteDoesNotReportSuccess() {
        resultStatus = 403;
        send();
        assertEquals(0, successes);
        assertEquals("configuration", failure);
    }

    @Test public void networkFailureDoesNotReportSuccess() {
        offline = true;
        send();
        assertEquals(0, successes);
        assertEquals("network", failure);
    }
}
