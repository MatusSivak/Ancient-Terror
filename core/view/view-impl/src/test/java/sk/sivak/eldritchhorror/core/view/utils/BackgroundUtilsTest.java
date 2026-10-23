package sk.sivak.eldritchhorror.core.view.utils;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.controller.GameController;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class BackgroundUtilsTest {

    @Test
    public void hidingBackgroundWhileMapIsVisibleDoesNotRestoreStaleEncounterBackground() {
        GameController controller = (GameController) Proxy.newProxyInstance(
                GameController.class.getClassLoader(), new Class[]{GameController.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getPreviousBackgroundName")) {
                        return "encounter/background/sea.jpg";
                    }
                    throw new AssertionError("Unexpected call: " + method.getName());
                });
        // A null stage fails the test if hideBackground tries to change the background.
        BackgroundUtils backgroundUtils = new BackgroundUtils(null, controller);
        AtomicBoolean completed = new AtomicBoolean();

        backgroundUtils.hideBackground().subscribe(() -> completed.set(true));

        assertTrue(completed.get());
    }
}
