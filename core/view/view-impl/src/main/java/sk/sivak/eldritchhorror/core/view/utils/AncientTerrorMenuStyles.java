package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.NEW_FONT_SOURCE_SERIF_4;

/** Shared startup and in-game menu button visuals. */
public final class AncientTerrorMenuStyles {
    private AncientTerrorMenuStyles() { }

    public static TextButton.TextButtonStyle button() {
        NinePatch normal = CustomAssetManager.createMenuButtonPatch(false);
        NinePatch pressed = CustomAssetManager.createMenuButtonPatch(true);
        normal.scale(0.5f, 0.5f);
        pressed.scale(0.5f, 0.5f);
        return button(normal, pressed);
    }

    public static TextButton.TextButtonStyle investigatorButton() {
        NinePatch normal = squarePatch("square_normal.png");
        NinePatch pressed = squarePatch("square_pressed.png");
        return button(normal, pressed);
    }

    private static NinePatch squarePatch(String file) {
        NinePatch patch = new NinePatch(CustomAssetManager.getTexture(
                "skin/ancient-terror/" + file), 48, 48, 48, 48);
        patch.setPadding(24, 24, 24, 24);
        patch.scale(50f / 256f, 50f / 256f);
        return patch;
    }

    private static TextButton.TextButtonStyle button(NinePatch normal, NinePatch pressed) {
        NinePatchDrawable up = new NinePatchDrawable(normal);
        NinePatchDrawable down = new NinePatchDrawable(pressed);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                up, down, down, CustomAssetManager.getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4));
        style.over = highlight(up);

        style.fontColor = new Color(0.91f, 0.85f, 0.69f, 1f);
        style.overFontColor = new Color(1f, 0.94f, 0.79f, 1f);

        style.downFontColor = new Color(0.82f, 0.75f, 0.59f, 1f);
        style.checkedFontColor = style.downFontColor;
        style.disabled = up;
        style.disabledFontColor = new Color(0.55f, 0.52f, 0.44f, 1f);
        style.pressedOffsetY = -1f;
        return style;
    }

    public static void addFocusHighlight(Button button) {
        Button.ButtonStyle style = button.getStyle();
        Drawable normal = style.up;
        button.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (actor == button) {
                    style.up = focused ? style.over : normal;
                }
            }
        });
    }

    /** A subtle inset gold edge, without allocating another texture. */
    public static Drawable highlight(Drawable background) {
        final TextureRegion white = CustomAssetManager.getSkin().getRegion("white");
        return new BaseDrawable(background) {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                background.draw(batch, x, y, width, height);
                float previousColor = batch.getPackedColor();
                float alpha = batch.getColor().a;
                batch.setColor(0.93f, 0.77f, 0.43f, alpha * 0.7f);
                float inset = 4f;
                float corner = Math.min(10f, height / 4f);
                batch.draw(white, x + corner, y + inset, width - 2 * corner, 1f);
                batch.draw(white, x + corner, y + height - inset - 1f, width - 2 * corner, 1f);
                batch.draw(white, x + inset, y + corner, 1f, height - 2 * corner);
                batch.draw(white, x + width - inset - 1f, y + corner, 1f, height - 2 * corner);
                batch.setColor(previousColor);
            }
        };
    }
}
