package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import java8.features.function.Supplier;

public class LightningImageActionBuilder {

    public static Action build(Actor actor, float delay, float power, float fadePower) {
        HighlightImage highlightImage = new HighlightImage(actor, () -> MathUtils.random(0, power));
        RepeatAction highlightWithDelay = Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                new VariableDelayAction(() -> MathUtils.random(0f, delay), Actions.run(highlightImage))
        ));
        return Actions.parallel(highlightWithDelay, new FadeAction(fadePower, 1-power));
    }

    private static class FadeAction extends Action {

        private final float fadePower;
        private final float minFade;

        public FadeAction(float fadePower, float minFade) {
            this.fadePower = fadePower;
            this.minFade = minFade;
        }

        @Override
        public boolean act(float v) {
            Color color = getActor().getColor();
            getActor().setColor(
                    Math.max(minFade, color.r - fadePower),
                    Math.max(minFade, color.g - fadePower),
                    Math.max(minFade, color.b - fadePower),
                    color.a);
            return false;
        }
    }
    private static class HighlightImage implements Runnable {

        private final Actor actor;
        private final Supplier<Float> powerSupplier;

        public HighlightImage(Actor actor, Supplier<Float> powerSupplier) {
            this.actor = actor;
            this.powerSupplier = powerSupplier;
        }

        @Override
        public void run() {
            Color color = actor.getColor();
            Float power = powerSupplier.get();
            actor.setColor(
                    Math.min(color.r + power, 1f),
                    Math.min(color.g + power, 1f),
                    Math.min(color.b + power, 1f),
                    color.a);
        }
    }

    public static class VariableDelayAction extends DelayAction {

        private final Supplier<Float> durationSupplier;

        public VariableDelayAction(Supplier<Float> durationSupplier, Action action) {
            super(durationSupplier.get());
            this.durationSupplier = durationSupplier;
            setAction(action);
        }

        @Override
        public void restart() {
            super.restart();
            setDuration(durationSupplier.get());
        }
    }

}
