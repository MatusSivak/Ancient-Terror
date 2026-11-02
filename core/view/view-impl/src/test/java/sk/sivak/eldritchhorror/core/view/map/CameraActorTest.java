package sk.sivak.eldritchhorror.core.view.map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import org.junit.Test;
import static org.junit.Assert.*;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;

public class CameraActorTest {
    @Test public void completesEarlyButQueuesNextMoveUntilArrival() {
        OrthographicCamera camera = new OrthographicCamera();
        Group root = new Group();
        CameraActor actor = new CameraActor(camera);
        root.addActor(actor);
        MoveToAction movement = Actions.moveTo(100, 80, 2);
        actor.addAction(movement);
        int[] calls = {0, 0};
        actor.beforeArrival(movement, .5f, () -> {
            calls[0]++;
            assertSame(root, actor.getParent());
            assertEquals(75, camera.position.x, .001f);
            actor.afterMovement(() -> {
                calls[1]++;
                assertNull(actor.getParent());
                assertEquals(100, camera.position.x, .001f);
            });
        });

        actor.act(1.25f);
        assertEquals(0, calls[0]);
        actor.act(.25f);
        assertEquals(1, calls[0]);
        assertEquals(0, calls[1]);
        actor.act(.5f);
        actor.act(1);
        assertEquals(1, calls[0]);
        assertEquals(1, calls[1]);
    }

    @Test public void shortMovesCompleteImmediatelyAndStillReachDestination() {
        OrthographicCamera camera = new OrthographicCamera();
        CameraActor actor = new CameraActor(camera);
        MoveToAction movement = Actions.moveTo(100, 80, .25f);
        actor.addAction(movement);
        int[] calls = {0};
        actor.beforeArrival(movement, .5f, () -> calls[0]++);

        actor.act(0);
        assertEquals(1, calls[0]);
        assertTrue(actor.hasActions());
        actor.act(.25f);
        assertEquals(100, camera.position.x, .001f);
        assertEquals(1, calls[0]);
    }

    @Test public void frameSkippingPastArrivalCompletesOnceBeforeQueuedMoves() {
        OrthographicCamera camera = new OrthographicCamera();
        CameraActor actor = new CameraActor(camera);
        MoveToAction movement = Actions.moveTo(100, 80, 2);
        actor.addAction(movement);
        int[] calls = {0};
        actor.beforeArrival(movement, .5f, () -> {
            calls[0]++;
            assertEquals(100, camera.position.x, .001f);
        });
        actor.afterMovement(() -> assertEquals(1, calls[0]));

        actor.act(5);
        actor.act(5);
        assertEquals(1, calls[0]);
    }

    @Test public void commitsAndDetachesBeforeCallbacksAndNeverWritesAgain() {
        OrthographicCamera camera = new OrthographicCamera();
        Group root = new Group();
        CameraActor first = new CameraActor(camera);
        root.addActor(first);
        first.setPosition(50, 40);
        first.addAction(Actions.moveTo(MAP_WIDTH + 20, 80, 1));
        int[] calls = {0};
        first.afterMovement(() -> {
            assertNull(first.getParent());
            assertEquals(20, camera.position.x, .001f);
            assertEquals(80, camera.position.y, .001f);
            calls[0]++;
            CameraActor next = new CameraActor(camera);
            next.setPosition(20, 80);
            next.addAction(Actions.moveTo(70, 120, 1));
            root.addActor(next);
        });
        first.act(.5f);
        assertEquals(0, calls[0]);
        first.act(.5f);
        root.act(1);
        first.act(1);
        assertEquals(1, calls[0]);
        assertEquals(70, camera.position.x, .001f);
        assertEquals(120, camera.position.y, .001f);
        assertEquals(0, root.getChildren().size);
    }
}
