package sk.sivak.eldritchhorror.core.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import java.lang.reflect.Proxy;
import org.junit.*;
import static org.junit.Assert.*;

public class MapDragListenerTest {
    private Input previousInput;
    private final boolean[] touched = new boolean[20];
    private final InputEvent event = new InputEvent();
    private final MapStage.MapDragListener listener = new MapStage.MapDragListener() {
        @Override public void dragStart(InputEvent event, float x, float y, int pointer) {}
        @Override public void drag(InputEvent event, float x, float y, int pointer) {}
    };

    @Before public void setup() {
        previousInput = Gdx.input;
        Gdx.input = (Input) Proxy.newProxyInstance(Input.class.getClassLoader(), new Class[]{Input.class},
                (proxy, method, args) -> touched[(Integer) args[0]]);
    }
    @After public void cleanup() { Gdx.input = previousInput; }

    @Test public void freshTouchRecoversWhenOverlayConsumedPreviousRelease() {
        touched[0] = true;
        assertTrue(listener.touchDown(event, 0, 0, 0, 0));
        listener.touchDragged(event, 10, 0, 0);
        assertTrue(listener.isDragging());
        assertTrue(listener.touchDown(event, 20, 0, 0, 0));
        assertFalse(listener.isDragging());
        listener.touchDragged(event, 30, 0, 0);
        assertTrue(listener.isDragging());
    }
    @Test public void releasedPointerCanBeReplacedButSecondLiveFingerCannotStealDrag() {
        touched[0] = true;
        assertTrue(listener.touchDown(event, 0, 0, 0, 0));
        touched[1] = true;
        assertFalse(listener.touchDown(event, 0, 0, 1, 0));
        touched[0] = false;
        assertTrue(listener.touchDown(event, 0, 0, 1, 0));
        listener.touchDragged(event, 10, 0, 1);
        assertTrue(listener.isDragging());
        listener.touchUp(event, 10, 0, 1, 0);
        assertFalse(listener.isDragging());
    }
    @Test public void cancellationAllowsNextGesture() {
        touched[0] = true;
        assertTrue(listener.touchDown(event, 0, 0, 0, 0));
        listener.cancel();
        touched[1] = true;
        assertTrue(listener.touchDown(event, 0, 0, 1, 0));
    }
}
