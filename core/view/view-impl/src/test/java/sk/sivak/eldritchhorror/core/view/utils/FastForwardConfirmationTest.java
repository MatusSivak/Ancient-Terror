package sk.sivak.eldritchhorror.core.view.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.lang.reflect.Proxy;
import org.junit.*;
import static org.junit.Assert.*;

public class FastForwardConfirmationTest {
    private Graphics previousGraphics;
    private com.badlogic.gdx.Files previousFiles;
    private boolean previousOn;
    private Stage stage;
    private Button button;
    private boolean held;
    private int confirmations;

    @Before public void setup() {
        previousGraphics = Gdx.graphics;
        previousFiles = Gdx.files;
        Gdx.files = (com.badlogic.gdx.Files) Proxy.newProxyInstance(com.badlogic.gdx.Files.class.getClassLoader(),
                new Class[]{com.badlogic.gdx.Files.class}, (proxy, method, args) -> null);
        previousOn = FastForwardAction.isOn();
        Gdx.graphics = (Graphics) Proxy.newProxyInstance(Graphics.class.getClassLoader(), new Class[]{Graphics.class},
                (proxy, method, args) -> method.getName().equals("getWidth") ? 800 : 600);
        Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class[]{Batch.class},
                (proxy, method, args) -> null);
        stage = new Stage(new ScreenViewport() {
            @Override public void update(int width, int height, boolean centerCamera) {
                setWorldSize(width, height);
                setScreenBounds(0, 0, width, height);
            }
        }, batch);
        button = new Button(new Button.ButtonStyle());
    }

    @After public void cleanup() {
        stage.dispose();
        Gdx.graphics = previousGraphics;
        Gdx.files = previousFiles;
        if (previousOn) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
    }

    @Test public void heldFreeRerollWaitsUntilAttachedEnabledAndTouchableThenFiresOnce() {
        watch(true);
        held = true;
        button.act(.1f);
        assertEquals(0, confirmations);
        stage.addActor(button);
        button.setDisabled(true);
        button.act(.1f);
        assertEquals(0, confirmations);
        button.setDisabled(false);
        button.setTouchable(Touchable.disabled);
        button.act(.1f);
        assertEquals(0, confirmations);
        button.setTouchable(Touchable.enabled);
        button.act(.1f);
        button.act(.1f);
        assertEquals(1, confirmations);
    }

    @Test public void releasedFastForwardDoesNotUseStaleSpeedFlagAndCanBeHeldLater() {
        stage.addActor(button);
        watch(true);
        FastForwardAction.turnOn();
        held = false;
        button.act(.1f);
        assertEquals(0, confirmations);
        held = true;
        button.act(.1f);
        button.act(.1f);
        assertEquals(1, confirmations);
    }

    @Test public void plainOkKeepsItsExistingArrivalOnlyBehavior() {
        stage.addActor(button);
        watch(false);
        button.act(.1f);
        held = true;
        button.act(.1f);
        assertEquals(0, confirmations);
    }

    private void watch(boolean freeReroll) {
        FastForwardAction.autoConfirmWhileHeld(button, () -> confirmations++, () -> held, freeReroll);
    }
}