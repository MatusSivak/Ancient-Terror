package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.CompletableSubscriber;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;

import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

/**
 * @author msivak
 */
public class ShowCurrentMysteryCardDialog extends Dialog {

    private CompletableSubscriber completableSubscriber;

    public ShowCurrentMysteryCardDialog(String title, Skin skin) {
        super(title, skin);

    }

    public void init(MysteryCardInfo mysteryCardInfo) {
        getContentTable().clear();
        Label label = new Label(mysteryCardInfo.getName(), getSkin());
        TextButton button = new TextButton(get("dialog.ok"), getSkin());
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
