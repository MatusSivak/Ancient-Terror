package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Preferences;

public class GridTestMomentumPreferences {
    static final String MOMENTUM_KEY = "atts.momentum";

    private final Preferences preferences;

    public GridTestMomentumPreferences(Preferences preferences) {
        if (preferences == null) {
            throw new IllegalArgumentException("preferences must not be null");
        }
        this.preferences = preferences;
    }

    public boolean load() {
        return preferences.getBoolean(MOMENTUM_KEY, false);
    }

    public void save(boolean enabled) {
        preferences.putBoolean(MOMENTUM_KEY, enabled);
        preferences.flush();
    }
}
