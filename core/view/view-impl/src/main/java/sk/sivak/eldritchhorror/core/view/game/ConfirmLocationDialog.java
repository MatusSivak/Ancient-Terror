package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class ConfirmLocationDialog extends Dialog {

    private CompletableSubscriber completableSubscriber;

    public ConfirmLocationDialog(String title, Skin skin) {
        super(title, skin);

    }

    public void init(LocationId locationId) {
        getContentTable().clear();
        Label label = new Label(locationId.name(), getSkin());
        TextButton button = new TextButton("OK", getSkin());
        button.addListener(new ButtonListener());
        getContentTable().add(label).row();
        getContentTable().add(button).row();
    }

    public void setCompletableSubscriber(CompletableSubscriber completableSubscriber) {
        this.completableSubscriber = completableSubscriber;
    }

    private class ButtonListener extends ClickListener {

        @Override
        public void clicked(InputEvent event, float x, float y) {
            hide();
            completableSubscriber.onCompleted();
        }
    }
}
