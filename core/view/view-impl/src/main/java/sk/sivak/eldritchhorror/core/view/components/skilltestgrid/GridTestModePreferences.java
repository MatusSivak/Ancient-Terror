package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Preferences;

public class GridTestModePreferences {
    static final String MODE_KEY = "atts.testMode";

    private final Preferences preferences;

    public GridTestModePreferences(Preferences preferences) {
        if (preferences == null) {
            throw new IllegalArgumentException("preferences must not be null");
        }
        this.preferences = preferences;
    }

    public TestMode load() {
        String savedMode = preferences.getString(MODE_KEY, TestMode.NORMAL.name());
        try {
            return TestMode.valueOf(savedMode);
        } catch (IllegalArgumentException invalidSavedMode) {
            return TestMode.NORMAL;
        }
    }

    public void save(TestMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode must not be null");
        }
        preferences.putString(MODE_KEY, mode.name());
        preferences.flush();
    }
}
