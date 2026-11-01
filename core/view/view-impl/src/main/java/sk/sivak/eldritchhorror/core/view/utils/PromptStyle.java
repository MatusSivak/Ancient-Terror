package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Quiet parchment notices shared by questions, hints and map status messages. */
public final class PromptStyle {
    public static final float MAX_WIDTH = 640f;
    public static final float PAD_X = 18f;
    public static final float PAD_Y = 10f;
    private static final Color INK = Color.valueOf("302A20");
    private static final Color BRASS = Color.valueOf("917346");
    private static final Color INNER_EDGE = Color.valueOf("C6AD7D");

    private PromptStyle() { }

    public static Label label(String text, boolean hint) {
        Label.LabelStyle style = new Label.LabelStyle(getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40), Color.WHITE);
        style.font.getData().markupEnabled = true;
        // Markup tints only the prose; embedded token artwork retains its original colors.
        Label label = new Label("[#" + INK + "]"
                + MarkupText.replaceColoredGlyphKeywords(text == null ? "" : text) + "[]", style);
        label.setFontScale(hint ? .42f : .5f);
        return label;
    }

    public static Drawable panel() {
        Texture button = getTexture(SQUARE_BUTTON_UP);
        // Reuse the unadorned parchment inside the game's existing brass button.
        TextureRegion paper = new TextureRegion(button, (int)(button.getWidth() * .2f),
                (int)(button.getHeight() * .23f), (int)(button.getWidth() * .6f),
                (int)(button.getHeight() * .5f));
        BaseDrawable panel = new BaseDrawable() {
            @Override public void draw(Batch batch, float x, float y, float width, float height) {
                float previous = batch.getPackedColor();
                float alpha = batch.getColor().a;
                fill(batch, x + 1, y - 2, width, height, 0.10f, 0.08f, 0.05f, .28f * alpha);
                batch.setColor(1, 1, 1, alpha);
                batch.draw(paper, x, y, width, height);
                frame(batch, x, y, width, height, BRASS, alpha);
                frame(batch, x + 3, y + 3, width - 6, height - 6, INNER_EDGE, alpha * .65f);
                batch.setColor(previous);
            }
        };
        panel.setLeftWidth(PAD_X);
        panel.setRightWidth(PAD_X);
        panel.setTopHeight(PAD_Y);
        panel.setBottomHeight(PAD_Y);
        return panel;
    }

    private static void frame(Batch batch, float x, float y, float width, float height, Color color, float alpha) {
        fill(batch, x, y, width, 1, color.r, color.g, color.b, alpha);
        fill(batch, x, y + height - 1, width, 1, color.r, color.g, color.b, alpha);
        fill(batch, x, y, 1, height, color.r, color.g, color.b, alpha);
        fill(batch, x + width - 1, y, 1, height, color.r, color.g, color.b, alpha);
    }

    private static void fill(Batch batch, float x, float y, float width, float height,
                             float r, float g, float b, float alpha) {
        batch.setColor(r, g, b, alpha);
        batch.draw(getTexture(PURE_WHITE_BACKGROUND), x, y, width, height);
    }
}
