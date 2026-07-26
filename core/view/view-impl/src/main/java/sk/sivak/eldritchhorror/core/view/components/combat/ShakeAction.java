package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import sk.sivak.eldritchhorror.core.view.utils.CircleUtils;

public class ShakeAction extends TemporalAction {

    float intensity;

    private float actorX;
    private float actorY;
    private float previousOffsetX = 0;
    private float previousOffsetY = 0;

    @Override
    protected void begin() {
        actorX = actor.getX();
        actorY = actor.getY();
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void update(float percent) {
        float centerIntensity = Math.abs(percent-0.5f) + 0.5f;

        actorX = actor.getX() - previousOffsetX;
        actorY = actor.getY() - previousOffsetY;

        Vector2 vector2 = CircleUtils.randomPointOnCircle(
                new Vector2(), intensity * centerIntensity, false);

        actor.setPosition(actorX + vector2.x, actorY + vector2.y);

        previousOffsetX = vector2.x;
        previousOffsetY = vector2.y;

    }

    @Override
    protected void end() {
        actor.setPosition(actor.getX() - previousOffsetX, actor.getY() - previousOffsetY);
    }
}