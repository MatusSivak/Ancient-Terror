package sk.sivak.eldritchhorror.core.view.firebase;

import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import static org.junit.Assert.*;

public class BugReportPayloadTest {
    @Test public void preservesSaveBytesAndEscapesDescription() {
        byte[] save = "{name:Akachi, note:\"Žltý znak\"}".getBytes(StandardCharsets.UTF_8);
        byte[] screenshot = new byte[]{0, 1, -1, 42};
        JsonValue fields = new JsonReader().parse(BugReportPayload.document("  Broken \"card\"\nŽ  ", save,
                screenshot, Collections.singletonMap("platform", "Android"), "player", 123)).get("fields");
        assertEquals("Broken \"card\"\nŽ", fields.get("description").getString("stringValue"));
        assertArrayEquals(save, Base64Coder.decode(fields.get("saveFile").getString("bytesValue")));
        assertArrayEquals(screenshot, Base64Coder.decode(fields.get("screenshotPng").getString("bytesValue")));
        assertEquals("player", fields.get("reporterUid").getString("stringValue"));
        assertEquals("Android", fields.get("metadata").get("mapValue").get("fields").get("platform").getString("stringValue"));
    }

    @Test public void missingAttachmentsAreOmitted() {
        JsonValue fields = new JsonReader().parse(BugReportPayload.document("No save yet", null, null,
                Collections.emptyMap(), "player", 123)).get("fields");
        assertFalse(fields.has("saveFile"));
        assertFalse(fields.has("screenshotPng"));
    }

    @Test(expected = IllegalArgumentException.class) public void rejectsBlankDescription() {
        BugReportPayload.document(" \n ", null, null, Collections.emptyMap(), "player", 0);
    }

    @Test(expected = IllegalArgumentException.class) public void rejectsOversizedSaveWithoutTruncating() {
        BugReportPayload.document("Bug", new byte[BugReportPayload.MAX_SAVE_BYTES + 1], null,
                Collections.emptyMap(), "player", 0);
    }

    @Test(expected = IllegalArgumentException.class) public void rejectsOversizedScreenshot() {
        BugReportPayload.document("Bug", null, new byte[BugReportPayload.MAX_SCREENSHOT_BYTES + 1],
                Collections.emptyMap(), "player", 0);
    }
}
