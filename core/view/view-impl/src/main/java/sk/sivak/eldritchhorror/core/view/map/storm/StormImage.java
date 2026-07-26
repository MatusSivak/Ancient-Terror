package sk.sivak.eldritchhorror.core.view.map.storm;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import sk.sivak.eldritchhorror.core.view.animation.AnimatedImage;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

public class StormImage extends AnimatedImage {

    public StormImage() {
        super(new Animation<>(0.05f, CustomAssetManager.getStormAnimation(), Animation.PlayMode.LOOP));
        setOrigin(200,200);
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.rotateBy(0.5f)));
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                Actions.color(Color.GRAY, 1f),
                Actions.color(Color.WHITE,0.5f)
        )));
        addAction(Actions.repeat(RepeatAction.FOREVER, Actions.sequence(
                Actions.scaleTo(1.1f, 1.1f, 0.75f, Interpolation.sine),
                Actions.scaleTo(1.0f, 1.0f, 0.75f, Interpolation.sine)
        )));
    }
}
