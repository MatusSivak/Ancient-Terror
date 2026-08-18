package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridTestMomentumPreferencesTest {

    @Test
    public void defaultsOffAndRestoresSavedSelection() {
        GridTestModePreferencesTest.MapPreferences preferences =
                new GridTestModePreferencesTest.MapPreferences();
        GridTestMomentumPreferences momentumPreferences =
                new GridTestMomentumPreferences(preferences);

        assertFalse(momentumPreferences.load());
        momentumPreferences.save(true);

        assertTrue(new GridTestMomentumPreferences(preferences).load());
    }
}
