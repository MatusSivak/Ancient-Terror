package sk.sivak.eldritchhorror.core.view.components.halloffame;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import static org.junit.Assert.assertEquals;

public class HallOfFameScreenTest {
    @Test public void resolvesMysteryKeysInSelectedLanguage() {
        String language = UiText.getLanguage();
        try {
            UiText.setLanguage("en");
            assertEquals("Seed of the Daemon Sultan", HallOfFameScreen.resolveMysteryName("mystery.azathoth.seed.name"));
            UiText.setLanguage("sk");
            assertEquals(UiText.get("mystery.azathoth.seed.name"), HallOfFameScreen.resolveMysteryName("mystery.azathoth.seed.name"));
        } finally {
            UiText.setLanguage(language);
        }
    }

    @Test public void preservesLegacyNames() {
        assertEquals("Seed of the Daemon Sultan", HallOfFameScreen.resolveMysteryName("Seed of the Daemon Sultan"));
    }
}
