package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.difficulty.DifficultyId;

/**
 * @author msivak
 */
public class SelectDifficultyDialog extends Dialog {

    private SingleSubscriber<? super DifficultyId> subscriber;

    public SelectDifficultyDialog(String title, Skin skin) {
        super(title, skin);
        for (DifficultyId difficultyId : DifficultyId.values()) {
            TextButton button = new TextButton(difficultyId.name(), skin);
            getContentTable().add(button);
            button.addListener(new SelectDifficultyDialog.ButtonListener(difficultyId));
        }
    }

    public void setSubscriber(SingleSubscriber<? super DifficultyId> subscriber) {
        this.subscriber = subscriber;
    }

    private class ButtonListener extends ClickListener {

        private DifficultyId difficultyId;

        public ButtonListener(DifficultyId difficultyId) {
            this.difficultyId = difficultyId;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            hide();
            subscriber.onSuccess(difficultyId);
        }
    }
}
