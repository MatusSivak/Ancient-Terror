package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import java.util.List;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;

/** A discarded monster recoils, folds into a thin silhouette, then sinks out of the world. */
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
                float y = actor.getY(), sx = actor.getScaleX(), sy = actor.getScaleY(), alpha = actor.getColor().a;
                actor.addAction(new FastForwardAction<>(new TemporalAction(1.15f * NORMAL_ACTION_DURATION) {
                    @Override protected void update(float percent) {
                        float collapse = MathUtils.clamp((percent - .2f) / .8f, 0f, 1f);
                        float recoil = MathUtils.sin(Math.min(percent / .2f, 1f) * MathUtils.PI) * .08f;
                        actor.setScale(sx * (1f + recoil) * (1f - Interpolation.pow2Out.apply(collapse)),
                                sy * (1f + recoil) * (1f - .85f * Interpolation.pow2In.apply(collapse)));
                        actor.setY(y - actor.getHeight() * .22f * collapse * collapse);
                        actor.getColor().a = alpha * (1f - collapse * collapse);
                    }
                    @Override protected void end() {
                        if (--remaining[0] == 0) {
                            remove.run();
                            subscriber.onCompleted();
                        }
                    }
                }));
            }
        });
    }
}
