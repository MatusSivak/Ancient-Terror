package sk.sivak.eldritchhorror.core.view.components.halloffame;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import static org.junit.Assert.assertEquals;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;

public class HallOfFameScreenTest {
    @Test public void awakenedYigUsesRemainingTimeIncludingTheReportedRecord() {
        assertEquals("23:40", HallOfFameScreen.formatDoomClock(AncientOneId.YIG, -4));
        assertEquals("23:20", HallOfFameScreen.formatDoomClock(AncientOneId.YIG, -8));
        assertEquals("23:55", HallOfFameScreen.formatDoomClock(AncientOneId.YIG, -1));
        assertEquals("00:00", HallOfFameScreen.formatDoomClock(AncientOneId.YIG, 0));
        assertEquals("23:10", HallOfFameScreen.formatDoomClock(AncientOneId.YIG, 10));
    }

    @Test public void ordinaryClockKeepsValidHoursAndMinutes() {
        assertEquals("22:45", HallOfFameScreen.formatDoomClock(AncientOneId.AZATHOTH, 15));
        assertEquals("23:00", HallOfFameScreen.formatDoomClock(AncientOneId.AZATHOTH, 12));
        assertEquals("00:00", HallOfFameScreen.formatDoomClock(AncientOneId.AZATHOTH, 0));
        assertEquals("00:00", HallOfFameScreen.formatDoomClock(AncientOneId.AZATHOTH, -1));
    }
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
