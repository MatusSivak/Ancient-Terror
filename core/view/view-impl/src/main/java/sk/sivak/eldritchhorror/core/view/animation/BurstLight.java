package sk.sivak.eldritchhorror.core.view.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import static sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager.*;

/** A quick pressure ring, central flash and radial embers, driven by the owner's clock. */
public final class BurstLight extends Actor {
    private final Color accent;
    private final Texture white = getTexture(PURE_WHITE_BACKGROUND), halo = getTexture("dice_shadow.png");
    private final float extent;
    private float progress;

    public BurstLight(Color accent, float extent) {
        this.accent = new Color(accent); this.extent = extent;
        setTouchable(Touchable.disabled); setVisible(false);
    }

    public void progress(float progress) { this.progress = progress; setVisible(true); }

    @Override public void draw(Batch batch, float parentAlpha) {
        float saved = batch.getPackedColor(), p = progress;
        float travel = Interpolation.pow3Out.apply(p), alpha = parentAlpha * getColor().a;
        float flash = Math.max(0, 1 - p * 7);
        float glowSize = extent * (.3f + .7f * travel);
        batch.setColor(accent.r, accent.g, accent.b, alpha * flash * .8f);
        batch.draw(halo, getX() - glowSize / 2, getY() - glowSize / 2, glowSize, glowSize);
        float radius = extent * (.06f + .64f * travel);
        for (int i = 0; i < 48; i++) {
            float a = i * 7.5f, b = a + 7.5f;
            line(batch, getX() + MathUtils.cosDeg(a) * radius, getY() + MathUtils.sinDeg(a) * radius,
                    (MathUtils.cosDeg(b) - MathUtils.cosDeg(a)) * radius,
                    (MathUtils.sinDeg(b) - MathUtils.sinDeg(a)) * radius, 1.5f + flash * 2,
                    alpha * Math.max(0, 1 - p * 3) * .8f);
        }
        for (int i = 0; i < 28; i++) {
            float angle = i * 137.508f, distance = extent * (.09f + (.38f + (i % 5) * .065f) * travel);
            float dx = MathUtils.cosDeg(angle), dy = MathUtils.sinDeg(angle);
            float length = extent * (.018f + (i % 3) * .009f) * (1 - p) + 1;
            line(batch, getX() + dx * distance, getY() + dy * distance - extent * .12f * p * p,
                    dx * length, dy * length, 1.2f + (i % 3) * .6f, alpha * (1 - p) * (1 - p));
        }
        batch.setColor(saved);
    }

    private void line(Batch batch, float x, float y, float dx, float dy, float width, float alpha) {
        batch.setColor(accent.r, accent.g, accent.b, alpha);
        batch.draw(white, x, y - width / 2, 0, width / 2, (float) Math.sqrt(dx * dx + dy * dy), width,
                1, 1, MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees,
                0, 0, white.getWidth(), white.getHeight(), false, false);
    }
}
