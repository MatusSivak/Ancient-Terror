package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;
import rx.SingleSubscriber;


/**
 * @author msivak
 */
public class NrPlayersDialog extends Dialog {

    public static final int MAX_NUMBER_OF_PLAYERS = 8;
    private static final float INVESTIGATOR_BUTTON_SIZE = 50f;
    private static final float INVESTIGATOR_BUTTON_SPACING = 6f;
    public static final int DIALOG_WIDTH = 530;
    public static final int DIALOG_HEIGHT = 147;
    private SingleSubscriber<? super Integer> subscriber;

    public NrPlayersDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
        getTitleLabel().setAlignment(Align.center, Align.center);
        getTitleLabel().setFontScale(0.4f);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setOrigin(Align.center);
        setModal(false);
        setMovable(false);
        setResizable(false);
        setKeepWithinStage(false);

        getContentTable().pad(0f);
        Table buttonRow = new Table();
        buttonRow.defaults().size(INVESTIGATOR_BUTTON_SIZE);

        for (int i = 1; i <= MAX_NUMBER_OF_PLAYERS; i++) {
            TextButton button = new TextButton(String.valueOf(i), createDialogButtonStyle());
            AncientTerrorMenuStyles.addFocusHighlight(button);
            button.setSize(INVESTIGATOR_BUTTON_SIZE, INVESTIGATOR_BUTTON_SIZE);
            button.getLabel().setAlignment(Align.center);
            button.getLabel().setFontScale(0.54f);
            button.pad(0f);
            Cell<TextButton> cell = buttonRow.add(button).size(INVESTIGATOR_BUTTON_SIZE);
            if (i < MAX_NUMBER_OF_PLAYERS) {
                cell.padRight(INVESTIGATOR_BUTTON_SPACING);
            }
            button.addListener(new ButtonListener(i));
        }

        getContentTable().add(buttonRow);
        getContentTable().pack();
        pack();
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        new InAppPurchaseManager().isProductPurchased("no_ads").subscribe();
    }

    public void setSubscriber(SingleSubscriber<? super Integer> subscriber) {
        this.subscriber = subscriber;
    }

    private TextButton.TextButtonStyle createDialogButtonStyle() {
        return AncientTerrorMenuStyles.investigatorButton();
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
