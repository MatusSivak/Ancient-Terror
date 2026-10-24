package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import sk.sivak.eldritchhorror.core.view.utils.UiSounds;

/** Menu/setup dialogs share short open/close cues, including keyboard and Back dismissal. */
public class SoundDialog extends Dialog {
    private boolean announced;

    public SoundDialog(String title, Skin skin) { super(title, skin); }
    public SoundDialog(String title, WindowStyle style) { super(title, style); }

    @Override public Dialog show(Stage stage, Action action) {
        UiSounds.attachButtons(this);
        super.show(stage, action);
        if (!announced) {
            announced = true;
            UiSounds.play(UiSounds.Cue.POPUP_OPEN);
        }
        return this;
    }

    @Override public void hide(Action action) {
        if (announced) {
            announced = false;
            UiSounds.play(UiSounds.Cue.POPUP_CLOSE);
        }
        super.hide(action);
    }
}
