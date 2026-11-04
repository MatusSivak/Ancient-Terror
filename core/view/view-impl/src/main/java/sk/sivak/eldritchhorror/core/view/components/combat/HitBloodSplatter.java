package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

/** Blood bursting out of a bullet wound: a spray along the shot, a mist puff and heavy drops that fall. */
final class HitBloodSplatter extends Actor {
    private static final float DURATION = 0.75f;
    private static final int DROPS = 30;

    private final Texture drop = CustomAssetManager.getTexture("dice_shadow.png");
    private final float[] angles = new float[DROPS];
    private final float[] speeds = new float[DROPS];
    private final float[] sizes = new float[DROPS];
    private final float[] reds = new float[DROPS];
    private final float scale;
    private float progress;

    private HitBloodSplatter(Vector2 impact, Vector2 shotDirection, float scale) {
        this.scale = scale;
        setPosition(impact.x, impact.y);
        setName("combat-hit-blood");
        setTouchable(Touchable.disabled);
        float shotAngle = shotDirection.isZero() ? 90f : shotDirection.angle();
        for (int i = 0; i < DROPS; i++) {
            // Most blood exits along the shot; some splashes back toward the shooter.
            boolean backSplash = i % 4 == 0;
            angles[i] = (backSplash ? shotAngle + 180f : shotAngle) + MathUtils.random(-55f, 55f);
            speeds[i] = MathUtils.random(45f, backSplash ? 90f : 150f) * scale;
            sizes[i] = MathUtils.random(4f, 11f) * scale;
            reds[i] = MathUtils.random(0.42f, 0.72f);
        }
    }

    static void spray(Group layer, Vector2 impact, Vector2 shotDirection, float scale) {
        HitBloodSplatter blood = new HitBloodSplatter(impact, shotDirection, scale);
        layer.addActor(blood);
        blood.addAction(new FastForwardAction<>(Actions.sequence(new TemporalAction(DURATION) {
            @Override protected void update(float percent) { blood.progress = percent; }
        }, Actions.removeActor())));
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float saved = batch.getPackedColor();
        float p = progress;
        float travel = Interpolation.pow2Out.apply(p);
        float fade = parentAlpha * (1f - Interpolation.pow2In.apply(p));

        float mist = (26f + 46f * travel) * scale;
        batch.setColor(0.45f, 0.02f, 0.03f, fade * 0.45f * (1f - p));
        batch.draw(drop, getX() - mist / 2f, getY() - mist / 2f, mist, mist);

        for (int i = 0; i < DROPS; i++) {
            float distance = speeds[i] * travel;
            float x = getX() + MathUtils.cosDeg(angles[i]) * distance;
            float y = getY() + MathUtils.sinDeg(angles[i]) * distance - 110f * scale * p * p;
            float width = sizes[i] * (1f - 0.35f * p);
            // Fast drops stretch into streaks while they fly, then round off as they slow.
            float length = width * (1f + 2.6f * (1f - p));
            batch.setColor(reds[i], 0.01f, 0.03f, fade * 0.95f);
            batch.draw(drop, x - length / 2f, y - width / 2f, length / 2f, width / 2f, length, width, 1f, 1f,
                    angles[i] - 25f * p, 0, 0, drop.getWidth(), drop.getHeight(), false, false);
        }
        batch.setColor(saved);
    }
}
