package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MAIN_MENU_DIALOG;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MAIN_MENU_DIALOG_BUTTON_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MAIN_MENU_DIALOG_BUTTON_PRESSED;

/**
 * @author msivak
 */
public class NrPlayersDialog extends Dialog {

    public static final int MAX_NUMBER_OF_PLAYERS = 8;
    private static final float INVESTIGATOR_BUTTON_SIZE = 56f;
    private static final float INVESTIGATOR_BUTTON_SPACING = 10f;
    private SingleSubscriber<? super Integer> subscriber;

    public NrPlayersDialog(String title, Skin skin) {
        super(title, skin);
        setBackground(CustomAssetManager.getTextureRegionDrawable(MAIN_MENU_DIALOG));
        setModal(true);
        setMovable(false);
        setResizable(false);
        setKeepWithinStage(false);

        getContentTable().padTop(20f).padBottom(16f);
        Table buttonRow = new Table();
        buttonRow.defaults().size(INVESTIGATOR_BUTTON_SIZE);

        for (int i = 1; i <= MAX_NUMBER_OF_PLAYERS; i++) {
            TextButton button = new TextButton(String.valueOf(i), createDialogButtonStyle());
            button.setSize(INVESTIGATOR_BUTTON_SIZE, INVESTIGATOR_BUTTON_SIZE);
            button.getLabel().setAlignment(Align.center);
            button.getLabel().setFontScale(0.62f);
            Cell<TextButton> cell = buttonRow.add(button).size(INVESTIGATOR_BUTTON_SIZE);
            if (i < MAX_NUMBER_OF_PLAYERS) {
                cell.padRight(INVESTIGATOR_BUTTON_SPACING);
            }
            button.addListener(new ButtonListener(i));
        }

        getContentTable().add(buttonRow).center();
        getContentTable().pack();
        pack();
        setSize(getPrefWidth(), getPrefHeight());
        new InAppPurchaseManager().isProductPurchased("no_ads").subscribe();
    }

    public void setSubscriber(SingleSubscriber<? super Integer> subscriber) {
        this.subscriber = subscriber;
    }

    private TextButton.TextButtonStyle createDialogButtonStyle() {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = CustomAssetManager.getTextureRegionDrawable(MAIN_MENU_DIALOG_BUTTON_NORMAL);
        style.down = CustomAssetManager.getTextureRegionDrawable(MAIN_MENU_DIALOG_BUTTON_PRESSED);
        style.over = style.up;
        style.font = CustomAssetManager.getBitmapFont(FONT_ADLER);
        style.fontColor = Color.WHITE;
        return style;
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
