package sk.sivak.eldritchhorror.core.view.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import rx.Single;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.AncientTerrorMenuStyles;

import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public final class RestartConfirmationDialog {
    private RestartConfirmationDialog() { }

    public static Single<Boolean> show(Stage stage) {
        return Single.create(subscriber -> {
            NinePatch background = CustomAssetManager.createMenuDialogPatch();
            background.scale(0.5f, 0.5f);
            Window.WindowStyle style = new Window.WindowStyle();
            style.background = new NinePatchDrawable(background);
            style.titleFont = CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4);
            style.titleFontColor = Color.valueOf("E8D9B0");
            Dialog dialog = new Dialog(get("menu.restartGame"), style) {
                private boolean answered;

                @Override
                protected void result(Object answer) {
                    if (answered) return;
                    answered = true;
                    if (!subscriber.isUnsubscribed()) subscriber.onSuccess(Boolean.TRUE.equals(answer));
                }
            };
            dialog.setMovable(false);
            dialog.getTitleLabel().setAlignment(Align.center);
            dialog.getTitleLabel().setFontScale(0.5f);
            Label question = new Label(get("init.restartQuestion"), new Label.LabelStyle(style.titleFont, Color.valueOf("F2E8CE")));
            question.setFontScale(0.4f);
            question.setWrap(true);
            question.setAlignment(Align.center);
            Label adNote = new Label(get("init.restartAdNote"), new Label.LabelStyle(style.titleFont, Color.valueOf("BEB69F")));
            adNote.setFontScale(0.3f);
            adNote.setWrap(true);
            adNote.setAlignment(Align.center);
            dialog.getContentTable().pad(20f, 16f, 22f, 16f);
            dialog.getContentTable().add(question).width(370f).padBottom(14f).row();
            dialog.getContentTable().add(adNote).width(370f);
            dialog.getButtonTable().defaults().width(180f).height(50f).pad(6f);
            dialog.button(button(get("dialog.no")), false);
            dialog.button(button(get("dialog.yes")), true);
            dialog.key(Input.Keys.ESCAPE, false);
            dialog.key(Input.Keys.BACK, false);
            dialog.show(stage);
        });
    }

    private static TextButton button(String text) {
        TextButton button = new TextButton(text, AncientTerrorMenuStyles.button());
        button.getLabel().setFontScale(0.4f);
        // Dialog uses the ChangeEvent to deliver the answer, so keep it enabled.
        // These one-use buttons should only look pressed while held.
        button.getStyle().checked = null;
        button.getStyle().checkedFontColor = null;
        AncientTerrorMenuStyles.addFocusHighlight(button);
        return button;
    }
}
