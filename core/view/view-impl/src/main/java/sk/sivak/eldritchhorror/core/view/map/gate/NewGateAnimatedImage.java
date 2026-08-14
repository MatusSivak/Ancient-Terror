package sk.sivak.eldritchhorror.core.view.map.gate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class NewGateAnimatedImage extends Group {

    private final Image backgroundImage;
    private final AnimatedImage gateAnimatedImage;
    private final GateColor gateColor;

    public NewGateAnimatedImage(GateColor gateColor, OmenColor omenColor) {
        this.gateColor = gateColor;
        setTouchable(Touchable.disabled);
        backgroundImage = new Image(CustomAssetManager.getTexture("gate/gate_border.png"));
        gateAnimatedImage = new AnimatedImage(new Animation<>(0.04f, CustomAssetManager.getGateAnimation(), Animation.PlayMode.LOOP));

        backgroundImage.setScale(0);
        gateAnimatedImage.setScale(0);

        Color tint = toLibgdxColor(gateColor);
        backgroundImage.setColor(tint.r, tint.g, tint.b, 0.2f);
        gateAnimatedImage.setColor(tint);

        backgroundImage.addAction(Actions.parallel(
                createPulseAction(2f, 2.2f),
                Actions.repeat(RepeatAction.FOREVER, Actions.rotateBy(360f, 8f)),
                Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                        Actions.alpha(0.6f, 1.2f, Interpolation.sineIn),
                        Actions.alpha(0.8f, 1.2f, Interpolation.sineOut)
                ))
        ));
        gateAnimatedImage.addAction(Actions.parallel(
                createPulseAction(1.3f, 1.45f),
                Actions.repeat(RepeatAction.FOREVER, Actions.rotateBy(360f, 8f))
        ));

        addActor(backgroundImage);
        addActor(gateAnimatedImage);
    }

    public Completable closeGate() {
        return Completable.create(onSub -> {
            gateAnimatedImage.clearActions();
            backgroundImage.clearActions();
            backgroundImage.addAction(Actions.parallel(
                    Actions.scaleTo(0f, 0f, 1.5f, Interpolation.exp5In),
                    Actions.fadeOut(1.5f, Interpolation.exp5In)
            ));
            gateAnimatedImage.addAction(Actions.sequence(
                    Actions.parallel(
                            Actions.scaleTo(0f, 0f, 1.5f, Interpolation.exp5In),
                            Actions.fadeOut(1.5f, Interpolation.exp5In)
                    ),
                    Actions.run(() -> {
                        gateAnimatedImage.remove();
                        backgroundImage.remove();
                        onSub.onCompleted();
                        remove();
                    })
            ));
        });
    }

    public void updateGateTransparency(OmenColor omenColor) {
        Color tint = toLibgdxColor(gateColor);
        if (omenColor != null && gateColor.equals(omenColor.toGateColor())) {
            gateAnimatedImage.setColor(tint.r, tint.g, tint.b, 1.0f);
            gateAnimatedImage.setFrameDuration(0.04f);
        } else {
            gateAnimatedImage.setColor(tint.r * 0.6f, tint.g * 0.6f, tint.b * 0.6f, 1.0f);
            gateAnimatedImage.setFrameDuration(0.08f);
        }
    }

    private static Color toLibgdxColor(GateColor gateColor) {
        switch (gateColor) {
            case RED:   return new Color(1f, 0.3f, 0.3f, 1f);
            case GREEN: return new Color(0.3f, 1f, 0.3f, 1f);
            case BLUE:  return new Color(0.3f, 0.6f, 1f, 1f);
            default:    return Color.WHITE.cpy();
        }
    }

    private RepeatAction createPulseAction(float minScale, float maxScale) {
        return Actions.repeat(
                RepeatAction.FOREVER, Actions.sequence(
                        Actions.scaleTo(minScale, minScale, 0.75f, Interpolation.sineIn),
                        Actions.scaleTo(maxScale, maxScale, 0.75f, Interpolation.sineOut)
                )
        );
    }

    @Override
    public void setOrigin(float originX, float originY) {
        super.setOrigin(originX, originY);
        for (Actor child : getChildren()) {
            child.setOrigin(originX, originY);
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        for (Actor child : getChildren()) {
            child.setSize(getWidth(), getHeight());
        }
    }
}
