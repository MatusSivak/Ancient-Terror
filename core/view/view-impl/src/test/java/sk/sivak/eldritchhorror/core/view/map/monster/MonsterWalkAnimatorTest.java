package sk.sivak.eldritchhorror.core.view.map.monster;

import com.badlogic.gdx.scenes.scene2d.Actor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MonsterWalkAnimatorTest {

    private static Actor monster() {
        Actor actor = new Actor();
        actor.setSize(100f, 100f);
        actor.setOrigin(50f, 50f);
        return actor;
    }

    @Test
    public void leansIntoDirectionOfTravel() {
        Actor actor = monster();
        MonsterWalkAnimator animator = new MonsterWalkAnimator();
        animator.update(actor, 1 / 60f);
        float tiltSum = 0f;
        for (int i = 0; i < 60; i++) {
            actor.setX(actor.getX() + 4f);
            animator.update(actor, 1 / 60f);
            tiltSum += animator.getTilt();
        }
        assertTrue(animator.isActive());
        // Walking right leans clockwise, i.e. negative rotation on average.
        assertTrue(tiltSum < 0f);
    }

    @Test
    public void drawOffsetsAreUndoneAndFadeOutWhenStopped() {
        Actor actor = monster();
        actor.setScale(1.1f, 0.9f);
        actor.setRotation(3f);
        MonsterWalkAnimator animator = new MonsterWalkAnimator();
        animator.update(actor, 1 / 60f);
        for (int i = 0; i < 20; i++) {
            actor.setX(actor.getX() + 3f);
            animator.update(actor, 1 / 60f);
        }
        animator.apply(actor);
        animator.restore(actor);
        assertEquals(60f, actor.getX(), 0.001f);
        assertEquals(0f, actor.getY(), 0.001f);
        assertEquals(3f, actor.getRotation(), 0.001f);
        assertEquals(1.1f, actor.getScaleX(), 0.001f);
        assertEquals(0.9f, actor.getScaleY(), 0.001f);

        for (int i = 0; i < 60; i++) {
            animator.update(actor, 1 / 60f);
        }
        assertFalse(animator.isActive());
    }
}
