package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.FADING_EFFECT_DURATION;

/**
 * @author msivak
 */
class ColorUtils {
    static void createColorAction(Actor actor, Color... colors) {
        SequenceAction sequenceAction = Actions.sequence();
        for (Color color : colors) {
            ColorAction action = new ColorAction();
            action.setEndColor(color);
            action.setDuration(FADING_EFFECT_DURATION);
            action.setInterpolation(Interpolation.sine);
            action.setActor(actor);
            sequenceAction.addAction(action);
        }
        actor.addAction(Actions.repeat(RepeatAction.FOREVER, sequenceAction));
    }
}
