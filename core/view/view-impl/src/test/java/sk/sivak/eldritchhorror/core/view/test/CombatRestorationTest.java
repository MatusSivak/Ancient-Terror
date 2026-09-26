package sk.sivak.eldritchhorror.core.view.test;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.components.combat.MonsterCombatTable;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class CombatRestorationTest {
    private Application previousApplication;
    private Files previousFiles;
    private boolean ownsSkin;

    @Before
    public void setUp() {
        previousApplication = Gdx.app;
        previousFiles = Gdx.files;
        Gdx.files = (Files) Proxy.newProxyInstance(Files.class.getClassLoader(),
                new Class<?>[]{Files.class}, (proxy, method, args) -> {
                    throw new UnsupportedOperationException(method.getName());
                });
        Gdx.app = (Application) Proxy.newProxyInstance(Application.class.getClassLoader(),
                new Class<?>[]{Application.class}, (proxy, method, args) -> {
                    if ("log".equals(method.getName())) return null;
                    throw new UnsupportedOperationException(method.getName());
                });
        ownsSkin = !VisUI.isLoaded();
        if (ownsSkin) VisUI.load(new Skin());
    }

    @After
    public void tearDown() {
        if (ownsSkin) VisUI.dispose();
        Gdx.app = previousApplication;
        Gdx.files = previousFiles;
    }

    @Test
    public void spellTestRestoresPanelAndUnblocksHorrorWithoutHidingAButton() throws Exception {
        TestViewImpl view = new TestViewImpl();
        TestCombatTable table = new TestCombatTable();
        table.setLocked(true);
        tables(view).push(table);
        AtomicBoolean highlighted = new AtomicBoolean();
        view.highlightHorrorCombat().subscribe(() -> highlighted.set(true));
        table.act(0.1f);
        assertFalse("The displaced panel reproduces the stuck horror command", highlighted.get());

        AtomicBoolean restored = new AtomicBoolean();
        view.restoreCombatAfterTest().subscribe(() -> restored.set(true));
        assertFalse(table.isLocked());
        assertFalse("Wait for the return animation", restored.get());
        table.act(0.1f);
        table.act(0.1f);
        assertTrue(restored.get());
        assertTrue(highlighted.get());
        assertTrue(table.horrorHighlighted);
        assertEquals(1, table.returnCount);
    }

    @Test
    public void ordinaryCombatResultDoesNotMovePanel() throws Exception {
        TestViewImpl view = new TestViewImpl();
        TestCombatTable table = new TestCombatTable();
        tables(view).push(table);
        AtomicBoolean completed = new AtomicBoolean();
        view.restoreCombatAfterTest().subscribe(() -> completed.set(true));
        assertTrue(completed.get());
        assertEquals(0, table.returnCount);
    }

    @Test
    public void testOutsideCombatCompletesImmediately() {
        AtomicBoolean completed = new AtomicBoolean();
        new TestViewImpl().restoreCombatAfterTest().subscribe(() -> completed.set(true));
        assertTrue(completed.get());
    }

    @SuppressWarnings("unchecked")
    private Stack<MonsterCombatTable> tables(TestViewImpl view) throws Exception {
        Field field = TestViewImpl.class.getDeclaredField("monsterCombatTableStack");
        field.setAccessible(true);
        return (Stack<MonsterCombatTable>) field.get(view);
    }

    /** Scene2D actions without textures or the global fast-forward UI. */
    private static class TestCombatTable extends MonsterCombatTable {
        int returnCount;
        boolean horrorHighlighted;

        @Override
        public Completable moveLeft() {
            return Completable.create(subscriber -> {
                returnCount++;
                addAction(Actions.run(() -> {
                    setCentered();
                    subscriber.onCompleted();
                }));
            });
        }

        @Override
        public void highlightHorror() {
            horrorHighlighted = true;
        }
    }
}
