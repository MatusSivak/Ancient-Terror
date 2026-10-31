package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import java.util.List;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;

/** A discarded monster vanishes into a spreading puff of smoke. */
public final class MonsterDiscardAnimation {
    private MonsterDiscardAnimation() { }

    public static Completable play(List<Actor> actors, Runnable remove) {
        return Completable.create(subscriber -> {
            if (actors.isEmpty()) {
                remove.run();
                subscriber.onCompleted();
                return;
            }
            int[] remaining = {actors.size()};
            for (Actor actor : actors) {
                if (actor instanceof MonsterImage) {
                    ((MonsterImage) actor).setSpawnAnimationActive(true);
                    ((MonsterImage) actor).prepareForDefeat();
                }
                if (actor instanceof EpicMonsterImage) {
                    ((EpicMonsterImage) actor).setSpawnAnimationActive(true);
                    ((EpicMonsterImage) actor).prepareForDefeat();
                }
                actor.clearActions();
                actor.setTouchable(Touchable.disabled);
                float sx = actor.getScaleX(), sy = actor.getScaleY(), alpha = actor.getColor().a;
                VanishPuff puff = new VanishPuff(actor);
                if (actor.getParent() != null) actor.getParent().addActor(puff);
                actor.addAction(new FastForwardAction<>(new TemporalAction(1.15f * NORMAL_ACTION_DURATION) {
                    @Override protected void update(float percent) {
                        float vanish = MathUtils.clamp(percent / .36f, 0f, 1f);
                        float scale = 1f - .25f * Interpolation.pow2In.apply(vanish);
                        actor.setScale(sx * scale, sy * scale);
                        actor.getColor().a = alpha * (1f - Interpolation.pow2Out.apply(vanish));
                        puff.progress = percent;
                    }
                    @Override protected void end() {
                        puff.remove();
                        if (--remaining[0] == 0) {
                            remove.run();
                            subscriber.onCompleted();
                        }
                    }
                }));
            }
        });
    }

    private static final class VanishPuff extends Actor {
        private final Vector2 center;
        private final float radius, alpha;
        private float progress;
        private Texture texture;

        VanishPuff(Actor actor) {
            setName("monster-discard-puff");
            setTouchable(Touchable.disabled);
            center = actor.localToParentCoordinates(new Vector2(actor.getWidth() / 2f, actor.getHeight() * .45f));
            radius = Math.max(actor.getWidth() * Math.abs(actor.getScaleX()),
                    actor.getHeight() * Math.abs(actor.getScaleY())) * .45f;
            alpha = actor.getColor().a;
        }

        @Override public void draw(Batch batch, float parentAlpha) {
            if (progress <= 0f) return;
            if (texture == null) texture = CustomAssetManager.getTexture(CustomAssetManager.GATE_SMOKE);
            Color saved = batch.getColor();
            float r = saved.r, g = saved.g, b = saved.b, a = saved.a;
            for (int i = 0; i < 10; i++) {
                float angle = i * 137.5f;
                float travel = Interpolation.pow2Out.apply(progress);
                float distance = radius * travel * (i == 0 ? 0f : .85f);
                float size = radius * (.8f + travel * .9f);
                float x = center.x + MathUtils.cosDeg(angle) * distance;
                float y = center.y + MathUtils.sinDeg(angle) * distance * .7f + radius * progress * .55f;
                float opacity = Math.min(1f, progress / .12f) * (1f - progress) * .6f * alpha * parentAlpha;
                batch.setColor(.55f, .64f, .57f, opacity);
                batch.draw(texture, x - size / 2f, y - size / 2f, size, size);
            }
            batch.setColor(r, g, b, a);
        }
    }
}
