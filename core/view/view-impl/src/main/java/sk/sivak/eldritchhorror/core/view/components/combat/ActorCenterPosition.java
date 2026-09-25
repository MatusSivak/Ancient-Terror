package sk.sivak.eldritchhorror.core.view.components.combat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Fireball source that follows an actor (e.g. a die), so each fireball starts from the actor's
 * current on-screen centre when it is thrown, not where the actor was when the fireballs were queued.
 * Uses identity equality because its coordinates change while it is a map key.
 */
public class ActorCenterPosition extends Vector2 {

    private final Actor actor;

    public ActorCenterPosition(Actor actor) {
        this.actor = actor;
        refresh();
    }

    public ActorCenterPosition refresh() {
        if (actor.getStage() != null) {
            set(actor.localToStageCoordinates(new Vector2(actor.getWidth() / 2f, actor.getHeight() / 2f)));
        }
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
