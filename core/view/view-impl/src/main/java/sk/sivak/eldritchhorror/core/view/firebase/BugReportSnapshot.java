package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import java.util.LinkedHashMap;
import java.util.Map;

/** The same attachments and device details for manual and automatic reports. */
public final class BugReportSnapshot {
    public byte[] save;
    public byte[] screenshot;
    public long capturedAt = System.currentTimeMillis();
    public Map<String, String> metadata = new LinkedHashMap<>();

    public static BugReportSnapshot capture(byte[] screenshot, String screenshotStatus, boolean bestEffort) {
        BugReportSnapshot snapshot = new BugReportSnapshot();
        snapshot.screenshot = screenshot;
        snapshot.metadata.put("screenshotStatus", screenshotStatus);
        snapshot.metadata.put("saveStatus", "missing");
        try {
            FileHandle file = Gdx.files.local("save.json");
            if (file.exists()) {
                snapshot.metadata.put("saveModifiedAt", Long.toString(file.lastModified()));
                if (file.length() > BugReportPayload.MAX_SAVE_BYTES) {
                    snapshot.metadata.put("saveStatus", "tooLarge");
                    if (!bestEffort) throw new IllegalArgumentException("saveTooLarge");
                } else {
                    snapshot.save = file.readBytes();
                    if (snapshot.save.length > BugReportPayload.MAX_SAVE_BYTES) {
                        snapshot.save = null;
                        snapshot.metadata.put("saveStatus", "tooLarge");
                        if (!bestEffort) throw new IllegalArgumentException("saveTooLarge");
                    } else snapshot.metadata.put("saveStatus", "attached");
                }
            }
        } catch (RuntimeException error) {
            if (!bestEffort) throw error;
            snapshot.metadata.put("saveStatus", "unavailable");
        }
        detail(snapshot, "platform", () -> Gdx.app.getType().name(), bestEffort);
        detail(snapshot, "platformVersion", () -> Integer.toString(Gdx.app.getVersion()), bestEffort);
        detail(snapshot, "language", UiText::getLanguage, bestEffort);
        detail(snapshot, "screen", () -> Gdx.graphics.getBackBufferWidth() + "x" + Gdx.graphics.getBackBufferHeight(), bestEffort);
        detail(snapshot, "density", () -> Float.toString(Gdx.graphics.getDensity()), bestEffort);
        detail(snapshot, "appVersion", () -> new JsonReader().parse(Gdx.files.internal("bug-report-firebase.json"))
                .getString("appVersion", "unknown"), bestEffort);
        return snapshot;
    }

    private static void detail(BugReportSnapshot snapshot, String key,
                               java.util.function.Supplier<String> value, boolean bestEffort) {
        try { snapshot.metadata.put(key, value.get()); }
        catch (RuntimeException error) {
            if (!bestEffort) throw error;
            snapshot.metadata.put(key, "unknown");
        }
    }
}
