package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GridTestControllerFocusTest {

    @Test
    public void startInitializesFocusToDefaultCount() {
        GridTestController controller = createController(new QueueSymbolProvider());
        controller.startTest(10);
        assertEquals(controller.getInitialFocusCount(), controller.getFocusRemaining());
    }

    @Test
    public void restartResetsFocusToDefaultCount() {
        GridTestController controller = createController(new QueueSymbolProvider());
        controller.setInitialFocusCount(3);
        controller.startTest(10);
        controller.useFocus();
        controller.useFocus();
        assertEquals(1, controller.getFocusRemaining());

        controller.startTest(10);
        assertEquals(3, controller.getFocusRemaining());
    }

    @Test
    public void useFocusDecrementsRemainingByOne() {
        GridTestController controller = createController(new QueueSymbolProvider());
        controller.setInitialFocusCount(3);
        controller.startTest(10);
        assertTrue(controller.useFocus());
        assertEquals(2, controller.getFocusRemaining());
        assertTrue(controller.useFocus());
        assertEquals(1, controller.getFocusRemaining());
        assertTrue(controller.useFocus());
        assertEquals(0, controller.getFocusRemaining());
        assertFalse(controller.useFocus());
        assertEquals(0, controller.getFocusRemaining());
    }

    @Test
    public void focusDoesNotConsumeMoves() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO
        ));
        controller.startTest(5);
        assertEquals(5, controller.getMovesRemaining());
        controller.useFocus();
        assertEquals(5, controller.getMovesRemaining());
    }

    @Test
    public void focusDoesNotAwardPoints() {
        GridTestController controller = createController(new QueueSymbolProvider());
        controller.startTest(10);
        assertEquals(0, controller.getSuccesses());
        controller.useFocus();
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void focusDoesNotChangeBoardContents() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO
        ));
        controller.setInitialFocusCount(3);
        controller.startTest(10);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO
        );
        GridBoard board = controller.getBoard();
        SymbolType cell00Before = board.getCell(0, 0);

        controller.useFocus();

        SymbolType cell00After = board.getCell(0, 0);
        assertEquals(cell00Before, cell00After);
    }

    @Test
    public void changingModeDoesNotResetFocusDuringActiveTest() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO
        ));
        controller.setInitialFocusCount(3);
        controller.startTest(10);
        controller.useFocus();
        controller.useFocus();
        assertEquals(1, controller.getFocusRemaining());

        controller.setSelectedMode(TestMode.BLESSED);

        assertEquals(1, controller.getFocusRemaining());
    }

    @Test
    public void restartAfterChangingModeResetsFocusAndAppliesMode() {
        GridTestController controller = createController(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO
        ));
        controller.setInitialFocusCount(3);
        controller.startTest(10);
        assertEquals(TestMode.NORMAL, controller.getActiveMode());
        controller.useFocus();
        controller.useFocus();

        controller.setSelectedMode(TestMode.BLESSED);
        assertEquals(1, controller.getFocusRemaining());

        controller.startTest(10);
        assertEquals(TestMode.BLESSED, controller.getActiveMode());
        assertEquals(3, controller.getFocusRemaining());
    }

    @Test
    public void focusWorksIdenticallyInAllModes() {
        for (TestMode mode : new TestMode[]{TestMode.BLESSED, TestMode.NORMAL, TestMode.CURSED}) {
            GridTestController controller = createController(new QueueSymbolProvider());
            controller.setSelectedMode(mode);
            controller.startTest(10);
            int initialFocusCount = controller.getInitialFocusCount();
            assertEquals(initialFocusCount, controller.getFocusRemaining());
            assertTrue(controller.useFocus());
            assertEquals(initialFocusCount - 1, controller.getFocusRemaining());
        }
    }

    @Test
    public void customInitialFocusCount() {
        GridTestController controller = createController(new QueueSymbolProvider());
        controller.setInitialFocusCount(5);
        controller.startTest(10);
        assertEquals(5, controller.getFocusRemaining());
        assertEquals(5, controller.getInitialFocusCount());
    }

    @Test
    public void invalidInitialFocusCountThrows() {
        GridTestController controller = createController(new QueueSymbolProvider());
        try {
            controller.setInitialFocusCount(-1);
            assertTrue("Should have thrown IllegalArgumentException", false);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("initialFocusCount must be >= 0"));
        }
    }

    private GridTestController createController(SymbolRandomProvider provider) {
        return new GridTestController(new GridBoard(provider));
    }

    static class QueueSymbolProvider implements SymbolRandomProvider {
        private final Deque<SymbolType> queue = new ArrayDeque<>();
        private final SymbolType[] fallback = new SymbolType[] {
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE, SymbolType.SIX
        };
        private int fallbackIndex;

        QueueSymbolProvider(SymbolType... symbols) {
            queue.addAll(Arrays.asList(symbols));
        }

        @Override
        public SymbolType peekNext() {
            if (!queue.isEmpty()) {
                return queue.peekFirst();
            }
            return fallback[fallbackIndex % fallback.length];
        }

        @Override
        public SymbolType next() {
            if (queue.isEmpty()) {
                SymbolType symbol = fallback[fallbackIndex % fallback.length];
                fallbackIndex++;
                return symbol;
            }
            return queue.removeFirst();
        }
    }
}
