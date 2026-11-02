package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import java.lang.reflect.Proxy;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class AutoConfirmTestActionTest {
    private Button button;
    private Files previousFiles;
    private final Group available = new Group();
    private final Group selected = new Group();
    private boolean held;
    private int confirmations;

    @Before public void setup() {
        previousFiles = Gdx.files;
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(), new Class[]{Files.class},
                (proxy, method, args) -> { throw new UnsupportedOperationException(method.getName()); });
        button = new Button(new Button.ButtonStyle());
    }

    @After public void cleanup() { Gdx.files = previousFiles; }

    private AutoConfirmTestAction action(int count) {
        return new AutoConfirmTestAction(button, available, selected, count, () -> held, () -> confirmations++);
    }

    @Test public void holdingFastForwardWithAllCardsSelectedConfirmsExactlyOnce() {
        selected.addActor(new WidgetGroup());
        held = true;
        AutoConfirmTestAction action = action(1);
        assertTrue(action.act(0));
        assertTrue(action.act(0));
        assertEquals(1, confirmations);
    }

    @Test public void normalSpeedWaitsAndPressingFastForwardLaterConfirms() {
        selected.addActor(new Actor());
        AutoConfirmTestAction action = action(1);
        assertFalse(action.act(0));
        assertEquals(0, confirmations);
        held = true;
        assertTrue(action.act(0));
        assertEquals(1, confirmations);
    }

    @Test public void bottomCardWaitsUntilItsDragFinishesAndSelectionIsUpdated() {
        Actor card = new Actor();
        available.addActor(card);
        held = true;
        AutoConfirmTestAction action = action(1);
        assertFalse(action.act(0));
        card.remove(); // The last available card is animating between rows.
        assertFalse(action.act(0));
        Actor placeholder = new Actor();
        placeholder.getColor().a = 0;
        selected.addActor(placeholder);
        assertFalse(action.act(0));
        placeholder.remove();
        selected.addActor(card);
        assertTrue(action.act(0));
        assertEquals(1, confirmations);
    }

    @Test public void returningACardToTheBottomPreventsConfirmation() {
        Actor card = new Actor();
        selected.addActor(card);
        held = true;
        AutoConfirmTestAction action = action(1);
        card.setTouchable(Touchable.disabled);
        assertFalse(action.act(0));
        available.addActor(card);
        card.setTouchable(Touchable.enabled);
        assertFalse(action.act(0));
        assertEquals(0, confirmations);
    }

    @Test public void disabledOrClosingButtonDoesNotConfirm() {
        selected.addActor(new Actor());
        held = true;
        AutoConfirmTestAction action = action(1);
        button.setDisabled(true);
        assertFalse(action.act(0));
        button.setDisabled(false);
        button.setTouchable(Touchable.disabled);
        assertFalse(action.act(0));
        assertEquals(0, confirmations);
    }
}
