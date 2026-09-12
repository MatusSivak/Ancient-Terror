package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** Lightweight frames shared by the investigator and Ancient One selection screens. */
public final class SelectionPanelStyle {
    private SelectionPanelStyle() { }

    public static Drawable panel(String fill, String edge) {
        final Color surface = Color.valueOf(fill);
        final Color border = Color.valueOf(edge);
        return new BaseDrawable() {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                float previous = batch.getPackedColor();
                float alpha = batch.getColor().a;
                batch.setColor(surface.r, surface.g, surface.b, surface.a * alpha);
                batch.draw(getTexture(PURE_WHITE_BACKGROUND), x, y, width, height);
                batch.setColor(border.r, border.g, border.b, border.a * alpha);
                batch.draw(getTexture(PURE_WHITE_BACKGROUND), x, y, width, 1f);
                batch.draw(getTexture(PURE_WHITE_BACKGROUND), x, y + height - 1f, width, 1f);
                batch.draw(getTexture(PURE_WHITE_BACKGROUND), x, y, 1f, height);
                batch.draw(getTexture(PURE_WHITE_BACKGROUND), x + width - 1f, y, 1f, height);
                batch.setColor(previous);
            }
        };
    }
}
