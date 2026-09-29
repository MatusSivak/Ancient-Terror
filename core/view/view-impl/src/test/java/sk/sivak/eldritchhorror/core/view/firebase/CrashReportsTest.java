package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class CrashReportsTest {
    @Rule public TemporaryFolder directory = new TemporaryFolder();
    private Files oldFiles;
    private Net oldNet;
    private Thread.UncaughtExceptionHandler oldHandler;
    private boolean offline = true;
    private int delegated;
    private final List<Net.HttpRequest> writes = new ArrayList<>();

    @Before public void setup() {
        oldFiles = Gdx.files;
        oldNet = Gdx.net;
        oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> delegated++);
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class[]{Files.class},
                (proxy, method, args) -> new FileHandle(new java.io.File(directory.getRoot(), (String) args[0])));
        Gdx.files.local("bug-report-firebase.json").writeString("{projectId:test,apiKey:test,appVersion:1}", false);
        Gdx.files.local("save.json").writeBytes(new byte[]{0, 1, 2, -1}, false);
        Gdx.net = (Net) Proxy.newProxyInstance(Net.class.getClassLoader(), new Class[]{Net.class}, (proxy, method, args) -> {
            if (!method.getName().equals("sendHttpRequest")) return null;
            Net.HttpRequest request = (Net.HttpRequest) args[0];
            Net.HttpResponseListener listener = (Net.HttpResponseListener) args[1];
            boolean auth = request.getUrl().contains("accounts:signUp");
            if (!auth) writes.add(request);
            if (offline) { listener.failed(new RuntimeException("offline")); return null; }
            Net.HttpResponse response = (Net.HttpResponse) Proxy.newProxyInstance(Net.HttpResponse.class.getClassLoader(),
                    new Class[]{Net.HttpResponse.class}, (p, m, a) -> {
                        if (m.getName().equals("getStatus")) return new HttpStatus(200);
                        if (m.getName().equals("getResultAsString")) return auth ? "{idToken:token,localId:user}" : "{}";
                        return null;
                    });
            listener.handleHttpResponse(response);
            return null;
        });
        CrashReports.install();
    }

    @After public void restore() {
        CrashReports.uninstall();
        Thread.setDefaultUncaughtExceptionHandler(oldHandler);
        Gdx.files = oldFiles;
        Gdx.net = oldNet;
    }

    @Test public void persistsOnceAndRetriesOriginalSnapshotOnRestart() {
        CrashReports.report(new IllegalStateException("original crash"));
        CrashReports.report(new IllegalStateException("duplicate"));
        FileHandle[] pending = Gdx.files.local("pending-crash-reports").list(".json");
        assertEquals(1, pending.length);
        CrashReports.Report saved = new Json().fromJson(CrashReports.Report.class, pending[0]);
        assertTrue(saved.description.contains("original crash"));
        assertTrue(saved.description.contains("CrashReportsTest"));
        assertArrayEquals(new byte[]{0, 1, 2, -1}, saved.snapshot.save);
        Gdx.files.local("save.json").writeString("new session", false);
        offline = false;
        CrashReports.uninstall();
        CrashReports.install();
        assertEquals(1, writes.size());
        assertTrue(writes.get(0).getUrl().endsWith("/crashReports?documentId=" + saved.id));
        JsonValue fields = new JsonReader().parse(writes.get(0).getContent()).get("fields");
        assertEquals("AAEC/w==", fields.get("saveFile").getString("bytesValue"));
        assertEquals(Long.toString(saved.snapshot.capturedAt), fields.get("capturedAt").getString("integerValue"));
        assertEquals(0, Gdx.files.local("pending-crash-reports").list(".json").length);
    }

    @Test public void uncaughtBackgroundCrashDelegatesAndSkipsGl() throws Exception {
        Thread worker = new Thread(() -> Thread.getDefaultUncaughtExceptionHandler()
                .uncaughtException(Thread.currentThread(), new AssertionError("background crash")), "worker");
        worker.start();
        worker.join();
        assertEquals(1, delegated);
        FileHandle[] pending = Gdx.files.local("pending-crash-reports").list(".json");
        assertEquals(1, pending.length);
        CrashReports.Report report = new Json().fromJson(CrashReports.Report.class, pending[0]);
        assertEquals("worker", report.snapshot.metadata.get("thread"));
        assertEquals("unavailable", report.snapshot.metadata.get("screenshotStatus"));
        assertNull(report.snapshot.screenshot);
    }

    @Test public void oversizedSaveDoesNotBlockCrashReporting() {
        Gdx.files.local("save.json").writeBytes(new byte[BugReportPayload.MAX_SAVE_BYTES + 1], false);
        CrashReports.report(new RuntimeException("crash"));
        FileHandle[] pending = Gdx.files.local("pending-crash-reports").list(".json");
        assertEquals(1, pending.length);
        CrashReports.Report report = new Json().fromJson(CrashReports.Report.class, pending[0]);
        assertNull(report.snapshot.save);
        assertEquals("tooLarge", report.snapshot.metadata.get("saveStatus"));
    }
}
