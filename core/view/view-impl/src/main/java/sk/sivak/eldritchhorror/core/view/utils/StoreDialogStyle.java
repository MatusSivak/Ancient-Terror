package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Shared look for the store-like dialogs (full-game offer, rating and video rewards). */
public final class StoreDialogStyle {
    public static final Color TEXT = Color.valueOf("E8D9B0");
    public static final Color MUTED = Color.valueOf("BEB69F");

    private StoreDialogStyle() {
    }

    public static Window.WindowStyle window() {
        NinePatch patch = CustomAssetManager.createMenuDialogPatch();
        patch.scale(0.5f, 0.5f);
        Window.WindowStyle style = new Window.WindowStyle();
        style.background = new NinePatchDrawable(patch);
        style.stageBackground = CustomAssetManager.getTextureRegionDrawable(
                CustomAssetManager.PURE_WHITE_BACKGROUND).tint(new Color(0f, 0f, 0f, 0.88f));
        style.titleFont = CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4);
        style.titleFontColor = TEXT;
        return style;
    }

    public static Label label(String text, float scale) {
        Label label = new Label(text, new Label.LabelStyle(
                CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4), TEXT));
        label.setWrap(true);
        label.setAlignment(Align.center);
        label.setFontScale(scale);
        return label;
    }

    public static TextButton primaryButton(String text) {
        TextButton button = new TextButton(text, AncientTerrorMenuStyles.button());
        button.getLabel().setFontScale(0.3f);
        button.getStyle().checked = null;
        button.getStyle().checkedFontColor = null;
        button.getStyle().up = AncientTerrorMenuStyles.highlight(button.getStyle().up);
        AncientTerrorMenuStyles.addFocusHighlight(button);
        return button;
    }

    public static TextButton textLink(String text, float scale) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = CustomAssetManager.getBitmapFontNew(CustomAssetManager.NEW_FONT_SOURCE_SERIF_4);
        style.fontColor = MUTED;
        style.overFontColor = Color.valueOf("FFF0CA");
        style.downFontColor = TEXT;
        style.disabledFontColor = Color.valueOf("777264");
        style.up = new BaseDrawable();
        style.over = AncientTerrorMenuStyles.highlight(style.up);
        TextButton button = new TextButton(text, style);
        button.getLabel().setFontScale(scale);
        AncientTerrorMenuStyles.addFocusHighlight(button);
        return button;
    }

    /** Scales and centers a transform-enabled dialog so the whole frame stays inside the viewport. */
    public static void fitToStage(Dialog dialog) {
        if (dialog.getStage() == null) return;
        float scale = Math.min(1f, Math.min(dialog.getStage().getWidth() * 0.88f / dialog.getWidth(),
                dialog.getStage().getHeight() * 0.88f / dialog.getHeight()));
        dialog.setOrigin(0f, 0f);
        dialog.setScale(scale);
        dialog.setPosition((dialog.getStage().getWidth() - dialog.getWidth() * scale) / 2f,
                (dialog.getStage().getHeight() - dialog.getHeight() * scale) / 2f);
    }
}
