package sk.sivak.eldritchhorror.core.view.map.gate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
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
import sk.sivak.eldritchhorror.core.view.game.InfoStage;

public class NewGateAnimatedImage extends Group {

    private final Image backgroundImage;
    private final AnimatedImage gateAnimatedImage;
    private final GateColor gateColor;
    private final Image omenHalo;
    private boolean matchingOmen;

    public NewGateAnimatedImage(GateColor gateColor, OmenColor omenColor) {
        this.gateColor = gateColor;
        setTouchable(Touchable.disabled);
        backgroundImage = new Image(CustomAssetManager.getTexture("gate/gate_border.png"));
        omenHalo = new Image(CustomAssetManager.getTexture("gate/gate_border.png"));
        omenHalo.setName("matching-omen-halo");
        omenHalo.setVisible(false);
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

        addActor(omenHalo);
        addActor(backgroundImage);
        addActor(gateAnimatedImage);
        updateGateTransparency(omenColor);
    }

    public Completable closeGate() {
        return Completable.create(onSub -> {
            if (getStage() == null) {
                onSub.onCompleted();
                return;
            }
            // Carry the visible map copy into the UI without jumping when the map is zoomed or wrapped.
            Vector2 center = toInfoCoordinates(getWidth() / 2f, getHeight() / 2f);
            Vector2 horizontal = toInfoCoordinates(getWidth() / 2f + 1f, getHeight() / 2f);
            Vector2 vertical = toInfoCoordinates(getWidth() / 2f, getHeight() / 2f + 1f);
            Group portrait = new Group();
            portrait.setName("closing-gate-portrait");
            // The halo extends beyond the gate's nominal bounds. Include it in the torn snapshots.
            portrait.setSize(getWidth() * 2.4f, getHeight() * 2.4f);
            portrait.setOrigin(portrait.getWidth() / 2f, portrait.getHeight() / 2f);
            portrait.setPosition(center.x - portrait.getWidth() / 2f, center.y - portrait.getHeight() / 2f);
            portrait.setScale(center.dst(horizontal), center.dst(vertical));
            gateAnimatedImage.clearActions();
            backgroundImage.clearActions();
            omenHalo.clearActions();
            omenHalo.setVisible(false);
            for (Image layer : new Image[]{backgroundImage, gateAnimatedImage}) {
                layer.setPosition((portrait.getWidth() - getWidth()) / 2f,
                        (portrait.getHeight() - getHeight()) / 2f);
                portrait.addActor(layer);
            }
            clearActions();
            remove();
            InfoStage.getStageSafe().addActor(portrait);
            GateDissipateAnimation.start(portrait, backgroundImage, gateAnimatedImage,
                    toLibgdxColor(gateColor), "gate-close", onSub);
        });
    }

    private Vector2 toInfoCoordinates(float x, float y) {
        Vector2 point = localToStageCoordinates(new Vector2(x, y));
        return InfoStage.getStageSafe().screenToStageCoordinates(getStage().stageToScreenCoordinates(point));
    }

    public void updateGateTransparency(OmenColor omenColor) {
        Color tint = toLibgdxColor(gateColor);
        boolean matches = omenColor != null && gateColor.equals(omenColor.toGateColor());
        if (matches != matchingOmen) {
            matchingOmen = matches;
            omenHalo.clearActions();
            omenHalo.setVisible(matches);
            if (matches) {
                omenHalo.setColor(tint);
                omenHalo.setRotation(0);
                omenHalo.addAction(Actions.forever(Actions.sequence(
                        Actions.scaleTo(1.35f, 1.35f), Actions.alpha(.95f),
                        Actions.parallel(Actions.scaleTo(3.2f, 3.2f, 1.25f, Interpolation.sineOut),
                                Actions.alpha(0, 1.25f, Interpolation.sineIn), Actions.rotateBy(-65, 1.25f)),
                        Actions.delay(.45f))));
            }
        }
        if (matches) {
            gateAnimatedImage.setColor(tint.r, tint.g, tint.b, 1.0f);
            gateAnimatedImage.setFrameDuration(0.04f);
        } else {
            gateAnimatedImage.setColor(tint.r * 0.6f, tint.g * 0.6f, tint.b * 0.6f, 1.0f);
            gateAnimatedImage.setFrameDuration(0.08f);
        }
    }

    public static Color toLibgdxColor(GateColor gateColor) {
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
        // Selection animates the gate size without resizing its surrounding panel.
        // Keep both rotating layers centered on their current bounds.
        setOrigin(getWidth() / 2f, getHeight() / 2f);
    }
}
