package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Preferences;

public class GridTestBlindPreferences {
    static final String BLIND_KEY = "atts.blind";

    private final Preferences preferences;

    public GridTestBlindPreferences(Preferences preferences) {
        if (preferences == null) {
            throw new IllegalArgumentException("preferences must not be null");
        }
        this.preferences = preferences;
    }

    public boolean load() {
        return preferences.getBoolean(BLIND_KEY, false);
    }

    public void save(boolean enabled) {
        preferences.putBoolean(BLIND_KEY, enabled);
        preferences.flush();
    }
}
