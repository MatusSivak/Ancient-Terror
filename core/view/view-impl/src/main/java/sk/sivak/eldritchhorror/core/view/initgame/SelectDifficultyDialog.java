package sk.sivak.eldritchhorror.core.view.initgame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.constants.difficulty.DifficultyId;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.FONT_ADLER;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MAIN_MENU_DIALOG;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MAIN_MENU_DIALOG_BUTTON_NORMAL;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.MAIN_MENU_DIALOG_BUTTON_PRESSED;

/**
 * @author msivak
 */
public class SelectDifficultyDialog extends Dialog {

    private SingleSubscriber<? super DifficultyId> subscriber;

    public SelectDifficultyDialog(String title, Skin skin) {
        super(title, skin);
        setBackground(CustomAssetManager.getTextureRegionDrawable(MAIN_MENU_DIALOG));
        setModal(true);
        setMovable(false);
        setResizable(false);
        setTransform(true);
        setScale(0.8f);
        getContentTable().pad(22f, 26f, 12f, 26f);
        getContentTable().defaults().space(12f);
        for (DifficultyId difficultyId : DifficultyId.values()) {
            TextButton button = new TextButton(difficultyId.name(), createDialogButtonStyle()) {
                @Override
                public float getPrefWidth() {
                    return 200f;
                }

                @Override
                public float getPrefHeight() {
                    return 46f;
                }
            };
            button.setTransform(true);
            button.setScale(0.34f);
            button.setSize(200f, 46f);
            button.getLabel().setFontScale(0.45f);
            getContentTable().add(button);
            button.addListener(new SelectDifficultyDialog.ButtonListener(difficultyId));
        }
        setSize(430f, 220f);
    }

    public void setSubscriber(SingleSubscriber<? super DifficultyId> subscriber) {
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
