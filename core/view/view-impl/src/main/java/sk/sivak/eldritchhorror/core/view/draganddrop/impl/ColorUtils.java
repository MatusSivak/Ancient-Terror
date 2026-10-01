package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
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

    /** Drop zone: faint tinted fill with a pulsing 2px outline in the actor's current color. */
    static void drawZone(Batch batch, Color color, float parentAlpha, float x, float y, float width, float height) {
        Texture texture = CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND);
        float alpha = color.a * parentAlpha;
        batch.setColor(color.r, color.g, color.b, alpha * 0.18f);
        batch.draw(texture, x, y, width, height);
        batch.setColor(color.r, color.g, color.b, alpha);
        float border = 2f;
        batch.draw(texture, x, y, width, border);
        batch.draw(texture, x, y + height - border, width, border);
        batch.draw(texture, x, y, border, height);
        batch.draw(texture, x + width - border, y, border, height);
        batch.setColor(Color.WHITE);
    }
}
