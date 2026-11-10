package sk.sivak.eldritchhorror.core.view.components.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class NoiseEffect extends AnimatedImage {


    private boolean strobeDisabled;
    private RepeatAction opacityPulse;

    public NoiseEffect() {
        super(new Animation<>(0.1f, CustomAssetManager.getNoiseAnimation(), Animation.PlayMode.LOOP));
        setTouchable(Touchable.disabled);

        Runnable strobeDisabledRunnable = () -> {
            if (Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled") && !strobeDisabled) {
                strobeDisabled = true;
                setBounds(0, 0, 0, 0);
            } else if (!Gdx.app.getPreferences("AncientTerror.xml").getBoolean("strobe_disabled") && strobeDisabled) {
                strobeDisabled = false;
                setBounds(0, 0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
            }
        };
        addAction(Actions.sequence(Actions.run(strobeDisabledRunnable),Actions.repeat(RepeatAction.FOREVER, Actions.delay(1f, Actions.run(strobeDisabledRunnable)))));
    }

    public void setTint(Color tint) {
        setColor(tint.r, tint.g, tint.b, getColor().a);
    }

    public void updateDoom(int doom) {
        removeAction(opacityPulse);
        float duration = 0.2f + Math.max(0, doom) * 0.1f;
        opacityPulse = Actions.forever(Actions.sequence(
                Actions.alpha(0.12f, duration),
                Actions.alpha(0.20f, duration)
        ));
        addAction(opacityPulse);
    }
}
