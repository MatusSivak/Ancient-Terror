package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridBoardTest {

    @Test
    public void horizontal555Match() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        assertEquals(1, board.findMatches().size());
    }

    @Test
    public void horizontal666Match() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.SIX, SymbolType.SIX, SymbolType.SIX,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        assertEquals(1, board.findMatches().size());
    }

    @Test
    public void vertical555Match() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.FIVE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.FIVE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FIVE, SymbolType.THREE, SymbolType.ONE
        );
        assertEquals(1, board.findMatches().size());
    }

    @Test
    public void vertical666Match() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.SIX, SymbolType.ONE, SymbolType.TWO,
                SymbolType.SIX, SymbolType.TWO, SymbolType.THREE,
                SymbolType.SIX, SymbolType.THREE, SymbolType.ONE
        );
        assertEquals(1, board.findMatches().size());
    }

    @Test
    public void oneTwoThreeTriplesMatchAndDiagonalDoesNotScore() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.THREE, SymbolType.TWO, SymbolType.SIX
        );
        assertEquals(1, board.findMatches().size());

        board.setBoard(
                SymbolType.TWO, SymbolType.TWO, SymbolType.TWO,
                SymbolType.ONE, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.SIX
        );
        assertEquals(1, board.findMatches().size());

        board.setBoard(
                SymbolType.THREE, SymbolType.THREE, SymbolType.THREE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.ONE, SymbolType.SIX
        );
        assertEquals(1, board.findMatches().size());

        board.setBoard(
                SymbolType.FIVE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.ONE, SymbolType.FIVE, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE
        );
        assertEquals(0, board.findMatches().size());
    }

    @Test
    public void mixedSimultaneousNeutralAndScoringMatch() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.TWO,
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE
        );
        assertEquals(2, board.findMatches().size());
    }

    @Test
    public void twoSimultaneousLinesScoreSeparately() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                SymbolType.FIVE, SymbolType.ONE, SymbolType.TWO,
                SymbolType.FIVE, SymbolType.TWO, SymbolType.THREE
        );
        assertEquals(2, board.findMatches().size());
    }

    @Test
    public void normalDetectsHorizontalAndVerticalButIgnoresDiagonals() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.THREE
        );
        assertEquals(1, board.findMatches(TestMode.NORMAL).size());

        board.setBoard(
                SymbolType.TWO, SymbolType.ONE, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.ONE
        );
        assertEquals(1, board.findMatches(TestMode.NORMAL).size());

        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.ONE, SymbolType.FIVE,
                SymbolType.THREE, SymbolType.FIVE, SymbolType.ONE
        );
        assertTrue(board.findMatches(TestMode.NORMAL).isEmpty());
    }

    @Test
    public void blessedDetectsHorizontalVerticalAndBothDiagonals() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.THREE
        );
        assertEquals(1, board.findMatches(TestMode.BLESSED).size());

        board.setBoard(
                SymbolType.TWO, SymbolType.ONE, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.ONE
        );
        assertEquals(1, board.findMatches(TestMode.BLESSED).size());

        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.ONE, SymbolType.FIVE,
                SymbolType.THREE, SymbolType.FIVE, SymbolType.ONE
        );
        assertEquals(1, board.findMatches(TestMode.BLESSED).size());

        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.THREE, SymbolType.FIVE, SymbolType.ONE
        );
        assertEquals(1, board.findMatches(TestMode.BLESSED).size());
    }

    @Test
    public void cursedDetectsBothDiagonalsButIgnoresHorizontalAndVertical() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.THREE
        );
        assertTrue(board.findMatches(TestMode.CURSED).isEmpty());

        board.setBoard(
                SymbolType.TWO, SymbolType.ONE, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.ONE
        );
        assertTrue(board.findMatches(TestMode.CURSED).isEmpty());

        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.ONE, SymbolType.FIVE,
                SymbolType.THREE, SymbolType.FIVE, SymbolType.ONE
        );
        assertEquals(1, board.findMatches(TestMode.CURSED).size());

        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FIVE,
                SymbolType.THREE, SymbolType.FIVE, SymbolType.ONE
        );
        assertEquals(1, board.findMatches(TestMode.CURSED).size());
    }

    @Test
    public void generatedBoardsExcludeOnlyMatchesAllowedByMode() {
        GridBoard normalBoard = new GridBoard(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.TWO,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO
        ));
        normalBoard.generateRandomBoard(TestMode.NORMAL);
        assertFalse(normalBoard.hasMatches(TestMode.NORMAL));

        GridBoard blessedBoard = new GridBoard(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.ONE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.THREE, SymbolType.ONE, SymbolType.TWO
        ));
        blessedBoard.generateRandomBoard(TestMode.BLESSED);
        assertFalse(blessedBoard.hasMatches(TestMode.BLESSED));

        GridBoard cursedBoard = new GridBoard(new QueueSymbolProvider(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.ONE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.ONE,
                SymbolType.ONE, SymbolType.ONE, SymbolType.ONE,
                SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR,
                SymbolType.THREE, SymbolType.FOUR, SymbolType.TWO
        ));
        cursedBoard.generateRandomBoard(TestMode.CURSED);
        assertFalse(cursedBoard.hasMatches(TestMode.CURSED));
        assertTrue(cursedBoard.hasMatches(TestMode.NORMAL));
    }

    @Test
    public void shiftLeftRightColumnUpDown() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );

        board.shift(new GridMove(GridMoveType.ROW_LEFT, 0), SymbolType.SIX);
        assertRow(board, 0, SymbolType.TWO, SymbolType.FIVE, SymbolType.SIX);

        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.FIVE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );
        board.shift(new GridMove(GridMoveType.ROW_RIGHT, 0), SymbolType.SIX);
        assertRow(board, 0, SymbolType.SIX, SymbolType.ONE, SymbolType.TWO);

        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.ONE,
                SymbolType.THREE, SymbolType.SIX, SymbolType.TWO
        );
        board.shift(new GridMove(GridMoveType.COLUMN_UP, 1), SymbolType.ONE);
        assertColumn(board, 1, SymbolType.FIVE, SymbolType.SIX, SymbolType.ONE);

        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.TWO, SymbolType.FIVE, SymbolType.ONE,
                SymbolType.THREE, SymbolType.SIX, SymbolType.TWO
        );
        board.shift(new GridMove(GridMoveType.COLUMN_DOWN, 1), SymbolType.ONE);
        assertColumn(board, 1, SymbolType.ONE, SymbolType.TWO, SymbolType.FIVE);
    }

    @Test
    public void rotateOuterClockwiseMovesOnlyPerimeterOnePosition() {
        GridBoard board = new GridBoard(new QueueSymbolProvider());
        board.setBoard(
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX,
                SymbolType.ONE, SymbolType.TWO, SymbolType.THREE
        );

        board.rotateOuterClockwise();

        assertRow(board, 0, SymbolType.FOUR, SymbolType.ONE, SymbolType.TWO);
        assertRow(board, 1, SymbolType.ONE, SymbolType.FIVE, SymbolType.THREE);
        assertRow(board, 2, SymbolType.TWO, SymbolType.THREE, SymbolType.SIX);
    }

    @Test
    public void peekNextDoesNotConsumeRandomSequence() {
        QueueSymbolProvider provider = new QueueSymbolProvider(SymbolType.FIVE, SymbolType.SIX);
        assertEquals(SymbolType.FIVE, provider.peekNext());
        assertEquals(SymbolType.FIVE, provider.peekNext());
        assertEquals(SymbolType.FIVE, provider.next());
        assertEquals(SymbolType.SIX, provider.peekNext());
    }

    private void assertRow(GridBoard board, int row, SymbolType a, SymbolType b, SymbolType c) {
        List<SymbolType> expected = Arrays.asList(a, b, c);
        List<SymbolType> actual = Arrays.asList(
                board.getCell(row, 0),
                board.getCell(row, 1),
                board.getCell(row, 2)
        );
        assertEquals(expected, actual);
    }

    private void assertColumn(GridBoard board, int column, SymbolType a, SymbolType b, SymbolType c) {
        List<SymbolType> expected = Arrays.asList(a, b, c);
        List<SymbolType> actual = Arrays.asList(
                board.getCell(0, column),
                board.getCell(1, column),
                board.getCell(2, column)
        );
        assertEquals(expected, actual);
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
