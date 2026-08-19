package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridTestControllerSpinTest {

    @Test
    public void startAndRestartResetConfiguredSpins() {
        GridTestController controller = createController();
        controller.setInitialSpinCount(2);

        controller.startTest(5);
        makeReady(controller);
        assertTrue(controller.beginSpin());
        assertEquals(1, controller.getRemainingSpins());

        controller.startTest(5);
        assertEquals(2, controller.getRemainingSpins());
    }

    @Test
    public void spinConsumesNoMoveAndCommitsOnlyWhenAnimationCompletes() {
        GridTestController controller = createController();
        controller.startTest(5);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        makeReady(controller);

        assertTrue(controller.beginSpin());
        assertEquals(5, controller.getMovesRemaining());
        assertEquals(SymbolType.ONE, controller.getBoard().getCell(0, 0));
        assertEquals(GridTestState.SPINNING, controller.getState());

        controller.completeSpin();

        assertEquals(SymbolType.FOUR, controller.getBoard().getCell(0, 0));
        assertEquals(SymbolType.FIVE, controller.getBoard().getCell(1, 1));
        assertEquals(5, controller.getMovesRemaining());
        assertEquals(0, controller.getSpinRemaining());
    }

    @Test
    public void spinIsAvailableOnlyWhileWaitingAndRemaining() {
        GridTestController controller = createController();
        controller.startTest(5);
        assertFalse(controller.canUseSpin());

        makeReady(controller);
        assertTrue(controller.canUseSpin());
        assertTrue(controller.beginSpin());
        assertFalse(controller.canUseSpin());
        assertFalse(controller.beginSpin());
        assertEquals(0, controller.getSpinRemaining());
    }

    @Test
    public void spinCannotStartAfterTestHasNoMovesRemaining() {
        GridTestController controller = createController();
        controller.startTest(0);
        makeReady(controller);

        assertFalse(controller.canUseSpin());
        assertFalse(controller.beginSpin());
        assertEquals(1, controller.getSpinRemaining());
    }

    @Test
    public void normalNeutralMatchCreatedBySpinAwardsMove() {
        GridTestController controller = createController();
        controller.startTest(5);
        controller.setDebugBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.ONE, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        );
        makeReady(controller);

        controller.beginSpin();
        controller.completeSpin();
        controller.resolveMatches(controller.findMatches());

        assertEquals(6, controller.getMovesRemaining());
        assertEquals(0, controller.getSuccesses());
    }

    @Test
    public void scoringMatchCreatedBySpinUsesMomentumRewards() {
        GridTestController controller = createController();
        controller.setConfiguredMomentum(true);
        controller.startTest(5);
        controller.setDebugBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.TWO,
                SymbolType.FIVE, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
        );
        makeReady(controller);

        controller.beginSpin();
        controller.completeSpin();
        controller.resolveMatches(controller.findMatches());

        assertEquals(1, controller.getSuccesses());
        assertEquals(6, controller.getMovesRemaining());
    }

    @Test
    public void spinMatchEvaluationUsesActiveMode() {
        for (TestMode mode : new TestMode[]{TestMode.BLESSED, TestMode.CURSED}) {
            GridTestController controller = createController();
            controller.setSelectedMode(mode);
            controller.startTest(5);
            controller.setDebugBoard(
                    SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                    SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                    SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR
            );
            makeReady(controller);

            controller.beginSpin();
            controller.completeSpin();

            assertEquals(1, controller.findMatches().size());
            assertEquals(SymbolType.FIVE, controller.findMatches().get(0).getSymbol());
        }
    }

    @Test
    public void nextTokenReservationSurvivesRefillDraws() {
        RandomSymbolProvider provider = new RandomSymbolProvider(new Random(42L));
        SymbolType revealed = provider.peekNext();

        provider.reserveNextToken();
        for (int i = 0; i < 12; i++) {
            provider.next();
            assertEquals(revealed, provider.peekNext());
        }
        provider.releaseNextToken();

        assertEquals(revealed, provider.peekNext());
        assertEquals(revealed, provider.next());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeInitialSpinCountIsRejected() {
        createController().setInitialSpinCount(-1);
    }

    private static GridTestController createController() {
        return new GridTestController(new GridBoard(new RandomSymbolProvider(new Random(123L))));
    }

    private static void makeReady(GridTestController controller) {
        controller.setState(GridTestState.WAITING_FOR_INPUT);
    }
}
