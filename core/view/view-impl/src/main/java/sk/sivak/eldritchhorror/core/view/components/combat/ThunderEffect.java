package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class ThunderEffect {

    private Actor actor;
    private static Image whiteImage;

    public ThunderEffect(Actor actor) {
        this.actor = actor;
    }

    public void execute() {
        ThunderEffectContext context = new ThunderEffectContext();
        actor.addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                new SingleBoltEffect(()-> MathUtils.random(0.75f, 1.5f), context),
                new VariableDelayAction(() -> MathUtils.random(0.2f, 1.5f))
        )));
        actor.addAction(new BrightnessDownAction(0.05f, context));
        actor.addAction(new ContextToAlphaAction(context));
    }

    private class ContextToAlphaAction extends Action {

        private boolean strobeDisabled;
        ThunderEffectContext context;

        public ContextToAlphaAction(ThunderEffectContext context) {
            this.context = context;
        }

        @Override
        public void setActor(Actor actor) {
            super.setActor(actor);
            Runnable strobeDisabledRunnable = () -> {
                strobeDisabled = Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled");
            };
            actor.addAction(Actions.sequence(Actions.run(strobeDisabledRunnable),Actions.repeat(RepeatAction.FOREVER, Actions.delay(1f, Actions.run(strobeDisabledRunnable)))));
            if (whiteImage != null) {
                whiteImage.remove();
            }
            whiteImage = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
            whiteImage.setSize(actor.getWidth(), actor.getHeight());
            whiteImage.setScale(actor.getScaleX(), actor.getScaleX());
            whiteImage.setOrigin(Align.center);
            whiteImage.getColor().a = 0.0f;
            whiteImage.setPosition(actor.getX(), actor.getY());
            if (actor.getParent() == null) {
                actor.getStage().addActor(whiteImage);
            } else {
                actor.getParent().addActorAfter(actor, whiteImage);
            }

        }

        @Override
        public boolean act(float v) {
            if (strobeDisabled) {
                whiteImage.getColor().a = 0f;
                actor.setColor(0.66f, 0.66f, 0.66f, actor.getColor().a);
                return false;
            }
            if (context.brightness > 1f) {
                whiteImage.getColor().a = context.brightness - 1;
                actor.setColor(1f, 1f, 1f, actor.getColor().a);
            } else {
                whiteImage.getColor().a = 0f;
                actor.setColor(context.brightness, context.brightness, context.brightness, actor.getColor().a);
            }
            return false;
        }
    }

    private class VariableDelayAction extends DelayAction {
        private final Supplier<Float> durationSupplier;

        public VariableDelayAction(Supplier<Float> durationSupplier) {
            this.durationSupplier = durationSupplier;
            setDuration(durationSupplier.get());
        }

        @Override
        public void restart() {
            super.restart();
            setDuration(durationSupplier.get());
        }
    }

    private class SingleBoltEffect extends TemporalAction {
        private final Supplier<Float> powerSupplier;
        private ThunderEffectContext context;
        private float newTemporalBrightness;

        public SingleBoltEffect(Supplier<Float> powerSupplier, ThunderEffectContext context) {
            super(0.1f);
            this.powerSupplier = powerSupplier;
            this.context = context;
        }

        @Override
        protected void update(float v) {
            context.brightness = newTemporalBrightness;
        }

        @Override
        protected void begin() {
            newTemporalBrightness = Math.min(2, context.brightness + powerSupplier.get());
        }
    }

    private class BrightnessDownAction extends Action {

        private float rate;
        private final ThunderEffectContext context;

        public BrightnessDownAction(float rate, ThunderEffectContext context) {
            this.rate = rate;
            this.context = context;
        }

        @Override
        public boolean act(float v) {
            context.brightness -= rate;
            context.brightness = Math.max(context.brightness, context.minBrightness);
            return false;
        }
    }

    private class ThunderEffectContext {
        private float brightness;
        private float minBrightness = 0.15f;
    }
}
