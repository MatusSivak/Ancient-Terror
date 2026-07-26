package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.SingleSubscriber;

/**
 * @author msivak
 */
public class NrPlayersDialog extends Dialog {

    public static final int MAX_NUMBER_OF_PLAYERS = 8;
    private SingleSubscriber<? super Integer> subscriber;

    public NrPlayersDialog(String title, Skin skin) {
        super(title, skin);
        for (int i = 1; i <= MAX_NUMBER_OF_PLAYERS; i++) {
            TextButton button = new TextButton(String.valueOf(i), skin);
            getContentTable().add(button);
            button.addListener(new ButtonListener(i));
        }
        new InAppPurchaseManager().isProductPurchased("no_ads").subscribe();
    }

    public void setSubscriber(SingleSubscriber<? super Integer> subscriber) {
        this.subscriber = subscriber;
    }

    private class ButtonListener extends ClickListener {

        private int value;

        public ButtonListener(int value) {
            this.value = value;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            hide();
            subscriber.onSuccess(value);
        }
    }
}
