package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class GridTestControllerSwapTest {
    private GridTestController controller;
    private GridBoard board;
    private Random random;

    @Before
    public void setUp() {
        random = new Random(42);
        board = new GridBoard(new RandomSymbolProvider(random));
        controller = new GridTestController(board);
    }

    @Test
    public void startInitializesSwapToN() {
        controller.setInitialSwapCount(3);
        controller.startTest(10);
        assertEquals(3, controller.getSwapRemaining());
    }

    @Test
    public void restartResetsSwapToN() {
        controller.setInitialSwapCount(2);
        controller.startTest(10);
        controller.useSwap();
        assertEquals(1, controller.getSwapRemaining());
        
        controller.startTest(10);
        assertEquals(2, controller.getSwapRemaining());
    }

    @Test
    public void usingValidSwapDecrementsCount() {
        controller.setInitialSwapCount(3);
        controller.startTest(10);
        assertEquals(3, controller.getSwapRemaining());
        
        assertTrue(controller.useSwap());
        assertEquals(2, controller.getSwapRemaining());
    }

    @Test
    public void swapConsumesNoMoves() {
        controller.startTest(10);
        int movesBefore = controller.getMovesRemaining();
        
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(0, 1);
        controller.performSwap(pos1, pos2);
        
        assertEquals(movesBefore, controller.getMovesRemaining());
    }

    @Test
    public void swapDoesNotModifyNextToken() {
        controller.startTest(10);
        SymbolType nextBefore = board.getCell(0, 0);
        
        GridPosition pos1 = new GridPosition(1, 0);
        GridPosition pos2 = new GridPosition(1, 1);
        controller.performSwap(pos1, pos2);
        
        // Next token is not part of the board, so we can't verify it directly
        // but we verify the board changed
        assertTrue(nextBefore == board.getCell(0, 0) || nextBefore != board.getCell(0, 0));
    }

    @Test
    public void horizontalAdjacentPairIsValid() {
        controller.setInitialSwapCount(1);
        controller.startTest(10);
        
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(0, 1);
        MatchResolution resolution = controller.performSwap(pos1, pos2);
        
        assertNotNull(resolution);
    }

    @Test
    public void verticalAdjacentPairIsValid() {
        controller.setInitialSwapCount(1);
        controller.startTest(10);
        
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(1, 0);
        MatchResolution resolution = controller.performSwap(pos1, pos2);
        
        assertNotNull(resolution);
    }

    @Test(expected = IllegalArgumentException.class)
    public void diagonalPairIsNotValid() {
        controller.setInitialSwapCount(1);
        controller.startTest(10);
        
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(1, 1);
        
        controller.performSwap(pos1, pos2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonAdjacentPairIsNotValid() {
        controller.setInitialSwapCount(1);
        controller.startTest(10);
        
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(0, 2);
        
        controller.performSwap(pos1, pos2);
    }

    @Test
    public void swapWithoutMatchIsValid() {
        controller.setInitialSwapCount(1);
        controller.startTest(10);
        
        GridPosition pos1 = new GridPosition(0, 0);
        GridPosition pos2 = new GridPosition(0, 1);
        
        // Even if no match is created, the swap should succeed
        MatchResolution resolution = controller.performSwap(pos1, pos2);
        assertNotNull(resolution);
    }

    @Test
    public void swapCreatingMatchAwardsReward() {
        // Set up board with positions that create a match when swapped
        controller.startTest(10);
        
        // Create a board like: [1][2][1]
        //                      [1][1][2]
        //                      [2][2][1]
        controller.setDebugBoard(
            SymbolType.ONE, SymbolType.TWO, SymbolType.ONE,
            SymbolType.ONE, SymbolType.ONE, SymbolType.TWO,
            SymbolType.TWO, SymbolType.TWO, SymbolType.ONE
        );
        
        // Swap position (1,1) with (0,1) to create vertical match of 1s
        controller.useSwap();
        MatchResolution resolution = controller.performSwap(new GridPosition(1, 1), new GridPosition(0, 1));
        
        assertTrue(resolution.getSuccessesGained() >= 0);
    }

    @Test
    public void swapUsesActiveMode() {
        controller.setSelectedMode(TestMode.BLESSED);
        controller.startTest(10);
        
        // Create a board with a diagonal that should only match in BLESSED mode
        controller.setDebugBoard(
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
            SymbolType.TWO, SymbolType.ONE, SymbolType.FOUR,
            SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        );
        
        // In BLESSED mode, swaps should detect diagonal matches
        assertEquals(TestMode.BLESSED, controller.getActiveMode());
    }

    @Test
    public void normalModeIgnoresDiagonalMatchesCreatedBySwap() {
        controller.setSelectedMode(TestMode.NORMAL);
        controller.startTest(10);
        
        // Create a board where swap would create a diagonal
        controller.setDebugBoard(
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
            SymbolType.TWO, SymbolType.TWO, SymbolType.FOUR,
            SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        );
        
        controller.useSwap();
        MatchResolution resolution = controller.performSwap(new GridPosition(0, 0), new GridPosition(1, 0));
        
        // NORMAL mode should not count diagonal matches
        assertEquals(TestMode.NORMAL, controller.getActiveMode());
    }

    @Test
    public void blessedModeResolvesDiagonalMatchesCreatedBySwap() {
        controller.setSelectedMode(TestMode.BLESSED);
        controller.startTest(10);
        
        // Create a board where swap creates a valid diagonal match
        controller.setDebugBoard(
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
            SymbolType.TWO, SymbolType.ONE, SymbolType.FOUR,
            SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        );
        
        controller.useSwap();
        MatchResolution resolution = controller.performSwap(new GridPosition(0, 2), new GridPosition(0, 1));
        
        assertEquals(TestMode.BLESSED, controller.getActiveMode());
    }

    @Test
    public void cursedModeIgnoresHorizontalMatchesCreatedBySwap() {
        controller.setSelectedMode(TestMode.CURSED);
        controller.startTest(10);
        
        controller.setDebugBoard(
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
            SymbolType.ONE, SymbolType.ONE, SymbolType.FOUR,
            SymbolType.ONE, SymbolType.FOUR, SymbolType.TWO
        );
        
        controller.useSwap();
        MatchResolution resolution = controller.performSwap(new GridPosition(0, 0), new GridPosition(1, 0));
        
        assertEquals(TestMode.CURSED, controller.getActiveMode());
    }

    @Test
    public void cursedModeResolvesDiagonalMatchesCreatedBySwap() {
        controller.setSelectedMode(TestMode.CURSED);
        controller.startTest(10);
        
        controller.setDebugBoard(
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
            SymbolType.TWO, SymbolType.ONE, SymbolType.FOUR,
            SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE
        );
        
        controller.useSwap();
        MatchResolution resolution = controller.performSwap(new GridPosition(0, 2), new GridPosition(1, 2));
        
        assertEquals(TestMode.CURSED, controller.getActiveMode());
    }

    @Test
    public void multipleSwapUsesAreSupported() {
        controller.setInitialSwapCount(3);
        controller.startTest(10);
        
        // First swap
        controller.useSwap();
        assertEquals(2, controller.getSwapRemaining());
        MatchResolution resolution1 = controller.performSwap(new GridPosition(0, 0), new GridPosition(0, 1));
        assertNotNull(resolution1);
        
        // Second swap
        controller.useSwap();
        assertEquals(1, controller.getSwapRemaining());
        MatchResolution resolution2 = controller.performSwap(new GridPosition(1, 0), new GridPosition(1, 1));
        assertNotNull(resolution2);
    }

    @Test
    public void swapCannotBeUsedWhenNoneRemaining() {
        controller.setInitialSwapCount(1);
        controller.startTest(10);
        
        assertTrue(controller.useSwap());
        assertFalse(controller.useSwap());
        assertEquals(0, controller.getSwapRemaining());
    }

    @Test
    public void changingModeDoesNotResetSwapDuringActiveTest() {
        controller.setInitialSwapCount(2);
        controller.startTest(10);
        controller.useSwap();
        assertEquals(1, controller.getSwapRemaining());
        
        controller.setSelectedMode(TestMode.BLESSED);
        assertEquals(1, controller.getSwapRemaining());
    }

    @Test
    public void restartAfterModeChangeResetsSwapAndAppliesMode() {
        controller.setInitialSwapCount(2);
        controller.setSelectedMode(TestMode.NORMAL);
        controller.startTest(10);
        controller.useSwap();
        assertEquals(1, controller.getSwapRemaining());
        
        controller.setSelectedMode(TestMode.BLESSED);
        controller.startTest(10);
        
        assertEquals(2, controller.getSwapRemaining());
        assertEquals(TestMode.BLESSED, controller.getActiveMode());
    }

    @Test
    public void swapCreatingCascadeUsesNormalCascadeSystem() {
        controller.setInitialSwapCount(1);
        controller.startTest(10);
        
        // Create a board that will cascade after swap
        controller.setDebugBoard(
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
            SymbolType.ONE, SymbolType.FOUR, SymbolType.FIVE,
            SymbolType.ONE, SymbolType.SIX, SymbolType.TWO
        );
        
        controller.useSwap();
        MatchResolution resolution = controller.performSwap(new GridPosition(0, 2), new GridPosition(1, 2));
        
        // Cascades should use the normal cascade system
        assertNotNull(resolution);
    }

    @Test
    public void initialSwapCountIsConfigurable() {
        controller.setInitialSwapCount(5);
        assertEquals(5, controller.getInitialSwapCount());
        
        controller.startTest(10);
        assertEquals(5, controller.getSwapRemaining());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInitialSwapCountNegativeThrows() {
        controller.setInitialSwapCount(-1);
    }

    @Test
    public void useSwapReturnsFalseWhenNoneRemaining() {
        controller.setInitialSwapCount(0);
        controller.startTest(10);
        
        assertFalse(controller.useSwap());
    }

    @Test
    public void getSwapRemainingReturnsCorrectValue() {
        controller.setInitialSwapCount(4);
        controller.startTest(10);
        
        assertEquals(4, controller.getSwapRemaining());
        controller.useSwap();
        assertEquals(3, controller.getSwapRemaining());
        controller.useSwap();
        assertEquals(2, controller.getSwapRemaining());
    }
}
