package sk.sivak.eldritchhorror.core.view.components.card;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.NORMAL_ACTION_DURATION;

/** Keep the card readable as it curls toward its recipient, then tuck it away. */
public final class CardCollectionAnimation {
    private CardCollectionAnimation() { }

    public static void play(Actor card, Vector2 destination, Runnable completed) {
        card.clearActions();
        card.setTouchable(Touchable.disabled);
        card.addAction(Actions.sequence(new FastForwardAction<>(flight(card, destination)),
                Actions.run(() -> { card.remove(); completed.run(); })));
    }

    static TemporalAction flight(Actor card, Vector2 destination) {
        final float startX = card.getX(), startY = card.getY();
        final float scaleX = card.getScaleX(), scaleY = card.getScaleY();
        final float rotation = card.getRotation(), alpha = card.getColor().a;
        final float endX = destination.x - card.getOriginX(), endY = destination.y - card.getOriginY();
        final float arc = Math.min(65f, Vector2.dst(startX, startY, endX, endY) * .2f);
        return new TemporalAction(.85f * NORMAL_ACTION_DURATION) {
            @Override protected void update(float percent) {
                float travel = Interpolation.sine.apply(percent);
                card.setPosition(MathUtils.lerp(startX, endX, travel),
                        MathUtils.lerp(startY, endY, travel) + MathUtils.sin(MathUtils.PI * travel) * arc);
                float shrink = MathUtils.lerp(1f, .035f, Interpolation.pow2Out.apply(percent));
                card.setScale(scaleX * shrink, scaleY * shrink);
                card.setRotation(rotation + 360f * travel);
                card.getColor().a = alpha * (1f - MathUtils.clamp((percent - .8f) / .2f, 0f, 1f));
            }
        };
    }
}
