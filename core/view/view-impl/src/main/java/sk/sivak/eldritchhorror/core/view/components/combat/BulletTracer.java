package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

final class BulletTracer {
    private BulletTracer() {
    }

    static void fire(Group layer, Vector2 shooter, Vector2 impact) {
        Vector2 towardShooter = new Vector2(shooter).sub(impact);
        Image tracer = new Image(CustomAssetManager.getSkin().getRegion("white"));
        tracer.setName("combat-bullet-tracer");
        tracer.setTouchable(Touchable.disabled);
        tracer.setColor(1f, 0.9f, 0.62f, 1f);
        tracer.setSize(towardShooter.len(), 3f);
        tracer.setOrigin(Align.left);
        tracer.setPosition(impact.x, impact.y, Align.left);
        tracer.setRotation(towardShooter.angle());
        layer.addActor(tracer);
        CombatSounds.playDiceFireballLaunch();
        tracer.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.delay(0.025f),
                Actions.parallel(Actions.scaleTo(0f, 0.4f, 0.12f), Actions.fadeOut(0.12f)),
                Actions.removeActor())));
    }
}
