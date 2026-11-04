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
        float opacity = parentAlpha * (1f - p * p);

        // A dark-red cloud swelling out of the burst.
        float mist = extent * (.35f + .9f * p);
        batch.setColor(.38f, .01f, .03f, parentAlpha * .55f * (1f - p) * (1f - p));
        batch.draw(drop, getX() - mist / 2, getY() - mist / 2, mist, mist);

        for (int i = 0; i < 120; i++) {
            float angle = i * 137.508f;
            float speed = extent * (.30f + (i % 9) * .075f);
            float x = getX() + MathUtils.cosDeg(angle) * speed * p;
            float y = getY() + MathUtils.sinDeg(angle) * speed * p - extent * .55f * p * p;
            float width = extent * (.020f + (i % 5) * .008f) * (1f - .4f * p);
            float length = width * (1.5f + 2.6f * (1f - p));
            batch.setColor(.40f + (i % 4) * .11f, .01f, .03f, opacity * .95f);
            batch.draw(drop, x - length / 2, y - width / 2, length / 2, width / 2,
                    length, width, 1, 1, angle, 0, 0, drop.getWidth(), drop.getHeight(), false, false);
        }
        // Heavy gobbets: thrown slower, falling harder.
        for (int i = 0; i < 16; i++) {
            float angle = i * 360f / 16 + (i % 3) * 11f;
            float speed = extent * (.22f + (i % 4) * .08f);
            float x = getX() + MathUtils.cosDeg(angle) * speed * p;
            float y = getY() + MathUtils.sinDeg(angle) * speed * p + extent * .12f * p - extent * .8f * p * p;
            float size = extent * (.05f + (i % 3) * .018f) * (1f - .3f * p);
            batch.setColor(.33f, .0f, .02f, opacity);
            batch.draw(drop, x - size / 2, y - size / 2, size / 2, size / 2,
                    size * 1.3f, size, 1, 1, angle + 90f * p, 0, 0, drop.getWidth(), drop.getHeight(), false, false);
        }
        batch.setColor(saved);
    }
}
