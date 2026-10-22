package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * Fallback input processor for the Android back key (and Escape on desktop).
 * Add it last to an input multiplexer so dialogs that handle the key themselves take precedence.
 */
public class BackKeyHandler extends InputAdapter {

    private final Runnable onBack;

    public BackKeyHandler(Runnable onBack) {
        this.onBack = onBack;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            onBack.run();
            return true;
        }
        return false;
    }
}
