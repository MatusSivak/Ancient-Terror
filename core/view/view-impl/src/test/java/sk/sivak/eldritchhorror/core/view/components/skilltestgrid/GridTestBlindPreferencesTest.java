package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridTestBlindPreferencesTest {

    @Test
    public void defaultsOffAndRestoresSavedSelection() {
        GridTestModePreferencesTest.MapPreferences preferences =
                new GridTestModePreferencesTest.MapPreferences();
        GridTestBlindPreferences blindPreferences =
                new GridTestBlindPreferences(preferences);

        assertFalse(blindPreferences.load());
        blindPreferences.save(true);

        assertTrue(new GridTestBlindPreferences(preferences).load());
    }
}
