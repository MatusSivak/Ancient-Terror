package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;

import java.util.List;

/**
 * @author msivak
 */
public class SelectAncientOneDialog extends Dialog {

    private SingleSubscriber<? super AncientOneInfo> subscriber;

    public SelectAncientOneDialog(String title, Skin skin) {
        super(title, skin);
    }

    void init(List<AncientOneInfo> ancientOnes) {
        for (AncientOneInfo ancientOne : ancientOnes) {
            TextButton button = new TextButton(ancientOne.getAncientOneId().name(), getSkin());
            getContentTable().add(button);
            button.addListener(new SelectAncientOneDialog.ButtonListener(ancientOne));
        }
    }

    public void setSubscriber(SingleSubscriber<? super AncientOneInfo> subscriber) {
        this.subscriber = subscriber;
    }

    private class ButtonListener extends ClickListener {

        private AncientOneInfo ancientOne;

        public ButtonListener(AncientOneInfo ancientOne) {
            this.ancientOne = ancientOne;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            hide();
            subscriber.onSuccess(ancientOne);
        }
    }
}
