package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import java.util.Map;

/** Attachments are stored as Firestore bytes, preserving the original save exactly. */
public final class BugReportPayload {
    public static final int MAX_DESCRIPTION = 4000;
    public static final int MAX_SAVE_BYTES = 512 * 1024;
    public static final int MAX_SCREENSHOT_BYTES = 200 * 1024;

    private BugReportPayload() { }

    public static String document(String description, byte[] save, byte[] screenshot,
                                  Map<String, String> metadata, String uid, long capturedAt) {
        String text = description == null ? "" : description.trim();
        if (text.isEmpty() || text.length() > MAX_DESCRIPTION) throw new IllegalArgumentException("description");
        if (save != null && save.length > MAX_SAVE_BYTES) throw new IllegalArgumentException("saveTooLarge");
        if (screenshot != null && screenshot.length > MAX_SCREENSHOT_BYTES) throw new IllegalArgumentException("screenshotTooLarge");
        JsonValue fields = object();
        fields.addChild("description", typed("stringValue", text));
        fields.addChild("reporterUid", typed("stringValue", uid));
        fields.addChild("capturedAt", typed("integerValue", Long.toString(capturedAt)));
        fields.addChild("schemaVersion", typed("integerValue", "1"));
        fields.addChild("status", typed("stringValue", "new"));
        JsonValue details = object();
        for (Map.Entry<String, String> entry : metadata.entrySet()) {
            details.addChild(entry.getKey(), typed("stringValue", entry.getValue()));
        }
        JsonValue map = object();
        map.addChild("fields", details);
        JsonValue value = object();
        value.addChild("mapValue", map);
        fields.addChild("metadata", value);
        if (save != null) fields.addChild("saveFile", typed("bytesValue", new String(Base64Coder.encode(save))));
        if (screenshot != null) fields.addChild("screenshotPng", typed("bytesValue", new String(Base64Coder.encode(screenshot))));
        JsonValue document = object();
        document.addChild("fields", fields);
        return document.toJson(JsonWriter.OutputType.json);
    }

    private static JsonValue object() { return new JsonValue(JsonValue.ValueType.object); }
    private static JsonValue typed(String type, String value) {
        JsonValue field = object();
        field.addChild(type, new JsonValue(value));
        return field;
    }
}
