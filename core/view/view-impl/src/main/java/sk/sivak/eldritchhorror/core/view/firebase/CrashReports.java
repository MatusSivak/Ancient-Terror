package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Persist first, then upload without depending on a surviving render loop. */
public final class CrashReports {
    private static volatile CrashReports active;
    private final Thread renderThread = Thread.currentThread();
    private final Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
    private final Thread.UncaughtExceptionHandler handler = this::uncaught;
    private final Set<String> uploading = new HashSet<>();
    private boolean captured;

    public static synchronized void install() {
        if (active != null) active.close();
        active = new CrashReports();
        Thread.setDefaultUncaughtExceptionHandler(active.handler);
        retryPending();
    }

    public static void report(Throwable error) {
        CrashReports reporter = active;
        if (reporter != null) reporter.capture(Thread.currentThread(), error);
    }

    public static void retryPending() {
        CrashReports reporter = active;
        if (reporter != null) reporter.flush();
    }

    public static synchronized void uninstall() {
        if (active != null) active.close();
        active = null;
    }

    private void close() {
        if (Thread.getDefaultUncaughtExceptionHandler() == handler) Thread.setDefaultUncaughtExceptionHandler(previous);
    }

    private void uncaught(Thread thread, Throwable error) {
        try { capture(thread, error); }
        finally {
            if (previous != null) previous.uncaughtException(thread, error);
            else error.printStackTrace(System.err);
        }
    }

    private synchronized void capture(Thread thread, Throwable error) {
        // One root failure per session, even if render() keeps failing or analytics forwards it again.
        if (captured) return;
        captured = true;
        try {
            byte[] screenshot = null;
            if (Thread.currentThread() == renderThread) {
                try { screenshot = BugReportScreenshot.capture(); }
                catch (Throwable ignored) { /* GL may already be unavailable. */ }
            }
            Report report = new Report();
            report.id = UUID.randomUUID().toString();
            report.snapshot = BugReportSnapshot.capture(screenshot, screenshot == null ? "unavailable" : "attached", true);
            report.snapshot.metadata.put("thread", thread.getName());
            StringWriter trace = new StringWriter();
            error.printStackTrace(new PrintWriter(trace));
            String description = trace.toString();
            report.description = description.length() <= BugReportPayload.MAX_DESCRIPTION ? description
                    : description.substring(0, BugReportPayload.MAX_DESCRIPTION - 16) + "\n[truncated]";
            FileHandle file = directory().child(report.id + ".json");
            try {
                FileHandle temporary = directory().child(report.id + ".tmp");
                temporary.writeString(new Json().toJson(report), false, "UTF-8");
                temporary.moveTo(file);
                log("Saved pending report " + report.id);
            } catch (Throwable ignored) { /* Still try uploading if local storage failed. */ }
            upload(report, file);
        } catch (Throwable ignored) {
            // Reporting must never replace the original failure or stop its existing handler.
        }
    }

    private FileHandle directory() { return Gdx.files.local("pending-crash-reports"); }

    private void flush() {
        try {
            for (FileHandle file : directory().list(".json")) {
                try { upload(new Json().fromJson(Report.class, file), file); }
                catch (Throwable ignored) { /* A damaged record must not block other reports. */ }
            }
        } catch (Throwable ignored) { /* Local storage may not be available yet. */ }
    }

    private synchronized void upload(Report report, FileHandle file) {
        if (!uploading.add(report.id)) return;
        BugReportSnapshot snapshot = report.snapshot;
        FirebaseBugReports.crashes().send(report.id, report.description, snapshot.save, snapshot.screenshot,
                snapshot.metadata, snapshot.capturedAt, new FirebaseBugReports.Callback() {
                    @Override public void success() {
                        synchronized (CrashReports.this) {
                            try { file.delete(); }
                            finally { uploading.remove(report.id); }
                            log("Uploaded crashReports/" + report.id);
                        }
                    }
                    @Override public void failed(String reason) {
                        synchronized (CrashReports.this) { uploading.remove(report.id); }
                        log("Upload failed (" + reason + ") for " + report.id + "; will retry on launch/resume");
                    }
                });
    }

    public static class Report {
        public String id;
        public String description;
        public BugReportSnapshot snapshot;
    }

    private static void log(String message) {
        try { if (Gdx.app != null) Gdx.app.log("CrashReports", message); }
        catch (Throwable ignored) { /* Logging must not interfere with crash handling. */ }
    }
}
