package sk.sivak.eldritchhorror.core.view.components.effect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class NoiseEffect extends AnimatedImage {


    private boolean strobeDisabled;
    private RepeatAction colorAnimation;
    private OmenColor omenColor = OmenColor.GREEN;
    private int doom = 0;

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

    public void updateOmenColor(OmenColor omenColor) {
        this.omenColor = omenColor;
        updateAnimation();
    }

    public void updateDoom(int doom) {
        this.doom = doom;
        updateAnimation();
    }

    private void updateAnimation() {
        removeAction(colorAnimation);
        colorAnimation = Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                Actions.color(omenColorToColor(), 0.2f + doom * 0.1f),
                Actions.color(new Color(1, 1, 1f, 0.2f), 0.2f + doom * 0.1f)
        ));
        addAction(colorAnimation);
    }

    private Color omenColorToColor() {
        switch (omenColor) {
            case GREEN:
                return new Color(0.20f, 1, 0.20f, 0.2f);
            case BLUE:
                return new Color(0.20f, 0.20f, 1, 0.2f);
            case RED:
                return new Color(1, 0.20f, 0.20f, 0.2f);
        }
        throw new IllegalArgumentException();
    }
}
