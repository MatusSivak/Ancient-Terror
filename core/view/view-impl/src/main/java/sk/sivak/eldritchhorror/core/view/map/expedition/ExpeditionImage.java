package sk.sivak.eldritchhorror.core.view.map.expedition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** A compass that settles into a gentle sway after its spin-in reveal. */
public final class ExpeditionImage extends Image {
    public ExpeditionImage(Texture texture) {
        super(texture);
    }

    public void startIdleAnimation() {
        addAction(Actions.forever(Actions.sequence(
                Actions.rotateTo(-15, 1f, Interpolation.sine),
                Actions.rotateTo(15, 1f, Interpolation.sine))));
    }
}
