package sk.sivak.eldritchhorror.core.view.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

/** Brief crimson spray, sharing the shatter clock and cached textures. */
final class DefeatBloodEffect extends Actor {
    private final float extent;
    private float progress;

    DefeatBloodEffect(float extent) {
        this.extent = extent;
        setName("defeat-blood");
        setTouchable(Touchable.disabled);
        setVisible(false);
    }

    void progress(float value) { progress = value; setVisible(true); }

    @Override public void draw(Batch batch, float parentAlpha) {
        Texture drop = CustomAssetManager.getTexture("dice_shadow.png");
        float saved = batch.getPackedColor(), p = progress;
        float opacity = parentAlpha * (1f - p) * (1f - p);
        for (int i = 0; i < 46; i++) {
            float angle = i * 137.508f;
            float speed = extent * (.36f + (i % 7) * .07f);
            float x = getX() + MathUtils.cosDeg(angle) * speed * p;
            float y = getY() + MathUtils.sinDeg(angle) * speed * p - extent * .4f * p * p;
            float width = extent * (.018f + (i % 4) * .006f) * (1f - .5f * p);
            float length = width * (1.5f + 2f * (1f - p));
            batch.setColor(.48f + (i % 3) * .13f, .015f, .035f, opacity * .95f);
            batch.draw(drop, x - length / 2, y - width / 2, length / 2, width / 2,
                    length, width, 1, 1, angle, 0, 0, drop.getWidth(), drop.getHeight(), false, false);
        }
        batch.setColor(saved);
    }
}
