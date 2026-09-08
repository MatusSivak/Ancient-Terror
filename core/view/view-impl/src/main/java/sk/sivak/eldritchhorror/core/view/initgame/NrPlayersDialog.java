package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MAIN_MENU_DIALOG_BUTTON_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MAIN_MENU_DIALOG_BUTTON_PRESSED;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;

/**
 * @author msivak
 */
public class NrPlayersDialog extends Dialog {

    public static final int MAX_NUMBER_OF_PLAYERS = 8;
    private static final float INVESTIGATOR_BUTTON_SIZE = 280f;
    private static final float INVESTIGATOR_BUTTON_SPACING = 10f;
    public static final int DIALOG_WIDTH = 2098;
    public static final int DIALOG_HEIGHT = 750;
    public static final int CELL_PAD_RIGHT = -50;
    private SingleSubscriber<? super Integer> subscriber;

    public NrPlayersDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
        getTitleLabel().setAlignment(Align.center, Align.center);
        getTitleLabel().setFontScale(1.5f);
        getTitleTable().padTop(315);
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setOrigin(Align.center);
        setModal(false);
        setMovable(false);
        setResizable(false);
        setKeepWithinStage(false);

        getContentTable().padTop(20f).padBottom(0f);
        Table buttonRow = new Table();
        buttonRow.defaults().size(INVESTIGATOR_BUTTON_SIZE);

        for (int i = 1; i <= MAX_NUMBER_OF_PLAYERS; i++) {
            TextButton button = new TextButton(String.valueOf(i), createDialogButtonStyle());
            button.setSize(INVESTIGATOR_BUTTON_SIZE, INVESTIGATOR_BUTTON_SIZE);
            button.getLabel().setAlignment(Align.center);
            button.getLabel().setFontScale(2.5f);
            button.padBottom(20f);
            Cell<TextButton> cell = buttonRow.add(button).size(INVESTIGATOR_BUTTON_SIZE);
            if (i < MAX_NUMBER_OF_PLAYERS) {
                cell.padRight(CELL_PAD_RIGHT);
            }
            button.addListener(new ButtonListener(i));
        }

        getContentTable().add(buttonRow).padTop(30);
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
        style.font = CustomAssetManager.getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4);
        style.fontColor = Color.valueOf("D2DADF");
        style.downFontColor = Color.valueOf("98A9B2");
        style.overFontColor = Color.valueOf("4F8DB7");
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
