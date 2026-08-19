package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GridBoard {
    public static final int SIZE = 3;
    private final SymbolType[][] board = new SymbolType[SIZE][SIZE];
    private final SymbolRandomProvider randomProvider;

    public GridBoard(SymbolRandomProvider randomProvider) {
        if (randomProvider == null) {
            throw new IllegalArgumentException("randomProvider must not be null");
        }
        this.randomProvider = randomProvider;
    }

    public void generateRandomBoard() {
        generateRandomBoard(TestMode.NORMAL);
    }

    public void generateRandomBoard(TestMode mode) {
        validateMode(mode);
        boolean hasMatches = true;
        while (hasMatches) {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    board[row][col] = randomProvider.next();
                }
            }
            hasMatches = !findMatches(mode).isEmpty();
        }
    }

    public void setBoard(SymbolType... cells) {
        if (cells == null || cells.length != SIZE * SIZE) {
            throw new IllegalArgumentException("Expected exactly " + (SIZE * SIZE) + " cells");
        }
        int index = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                SymbolType symbol = cells[index++];
                if (symbol == null) {
                    throw new IllegalArgumentException("Board cell must not be null");
                }
                board[row][col] = symbol;
            }
        }
    }

    public SymbolType getCell(int row, int column) {
        validateRow(row);
        validateColumn(column);
        return board[row][column];
    }

    public GridShiftOutcome shift(GridMove move) {
        if (move == null) {
            throw new IllegalArgumentException("move must not be null");
        }
        SymbolType incoming = randomProvider.next();
        return shift(move, incoming);
    }

    GridShiftOutcome shift(GridMove move, SymbolType forcedIncoming) {
        if (move == null) {
            throw new IllegalArgumentException("move must not be null");
        }
        if (forcedIncoming == null) {
            throw new IllegalArgumentException("forcedIncoming must not be null");
        }
        switch (move.getType()) {
            case ROW_LEFT:
                shiftRowLeft(move.getIndex(), forcedIncoming);
                break;
            case ROW_RIGHT:
                shiftRowRight(move.getIndex(), forcedIncoming);
                break;
            case COLUMN_UP:
                shiftColumnUp(move.getIndex(), forcedIncoming);
                break;
            case COLUMN_DOWN:
                shiftColumnDown(move.getIndex(), forcedIncoming);
                break;
            default:
                throw new IllegalArgumentException("Unsupported move type: " + move.getType());
        }
        return new GridShiftOutcome(move, forcedIncoming);
    }

    private void shiftRowLeft(int row, SymbolType incoming) {
        validateRow(row);
        shiftArrayLeft(board[row], incoming);
    }

    private void shiftRowRight(int row, SymbolType incoming) {
        validateRow(row);
        shiftArrayRight(board[row], incoming);
    }

    private void shiftColumnUp(int column, SymbolType incoming) {
        validateColumn(column);
        SymbolType[] column_array = new SymbolType[SIZE];
        for (int i = 0; i < SIZE; i++) {
            column_array[i] = board[i][column];
        }
        shiftArrayLeft(column_array, incoming);
        for (int i = 0; i < SIZE; i++) {
            board[i][column] = column_array[i];
        }
    }

    private void shiftColumnDown(int column, SymbolType incoming) {
        validateColumn(column);
        SymbolType[] column_array = new SymbolType[SIZE];
        for (int i = 0; i < SIZE; i++) {
            column_array[i] = board[i][column];
        }
        shiftArrayRight(column_array, incoming);
        for (int i = 0; i < SIZE; i++) {
            board[i][column] = column_array[i];
        }
    }

    private void shiftArrayLeft(SymbolType[] array, SymbolType incoming) {
        for (int i = 0; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        array[array.length - 1] = incoming;
    }

    private void shiftArrayRight(SymbolType[] array, SymbolType incoming) {
        for (int i = array.length - 1; i > 0; i--) {
            array[i] = array[i - 1];
        }
        array[0] = incoming;
    }

    public void swap(GridPosition pos1, GridPosition pos2) {
        if (pos1 == null || pos2 == null) {
            throw new IllegalArgumentException("Positions must not be null");
        }
        validateRow(pos1.getRow());
        validateColumn(pos1.getColumn());
        validateRow(pos2.getRow());
        validateColumn(pos2.getColumn());
        
        SymbolType temp = board[pos1.getRow()][pos1.getColumn()];
        board[pos1.getRow()][pos1.getColumn()] = board[pos2.getRow()][pos2.getColumn()];
        board[pos2.getRow()][pos2.getColumn()] = temp;
    }

    public List<GridMatch> findMatches() {
        return findMatches(TestMode.NORMAL);
    }

    public List<GridMatch> findMatches(TestMode mode) {
        validateMode(mode);
        List<GridMatch> matches = new ArrayList<>();

        if (mode.allows(GridMatchOrientation.HORIZONTAL)) {
            for (int row = 0; row < SIZE; row++) {
                if (isThreeOfAKind(board[row][0], board[row][1], board[row][2])) {
                    List<GridPosition> cells = new ArrayList<>(SIZE);
                    for (int col = 0; col < SIZE; col++) {
                        cells.add(new GridPosition(row, col));
                    }
                    matches.add(new GridMatch(board[row][0], cells));
                }
            }
        }

        if (mode.allows(GridMatchOrientation.VERTICAL)) {
            for (int column = 0; column < SIZE; column++) {
                if (isThreeOfAKind(board[0][column], board[1][column], board[2][column])) {
                    List<GridPosition> cells = new ArrayList<>(SIZE);
                    for (int row = 0; row < SIZE; row++) {
                        cells.add(new GridPosition(row, column));
                    }
                    matches.add(new GridMatch(board[0][column], cells));
                }
            }
        }

        if (mode.allows(GridMatchOrientation.DIAGONAL)) {
            addDiagonalMatch(matches, 0, SIZE - 1);
            addDiagonalMatch(matches, SIZE - 1, 0);
        }

        return matches;
    }

    private void addDiagonalMatch(List<GridMatch> matches, int startColumn, int endColumn) {
        int middleColumn = SIZE / 2;
        if (!isThreeOfAKind(board[0][startColumn], board[1][middleColumn], board[2][endColumn])) {
            return;
        }
        List<GridPosition> cells = new ArrayList<>(SIZE);
        cells.add(new GridPosition(0, startColumn));
        cells.add(new GridPosition(1, middleColumn));
        cells.add(new GridPosition(2, endColumn));
        matches.add(new GridMatch(board[0][startColumn], cells));
    }

    private boolean isThreeOfAKind(SymbolType first, SymbolType second, SymbolType third) {
        return first == second && first == third;
    }

    public boolean hasMatches() {
        return !findMatches().isEmpty();
    }

    public boolean hasMatches(TestMode mode) {
        return !findMatches(mode).isEmpty();
    }

    public Set<GridPosition> collectMatchedCells(List<GridMatch> matches) {
        Set<GridPosition> positions = new LinkedHashSet<>();
        for (GridMatch match : matches) {
            positions.addAll(match.getCells());
        }
        return positions;
    }

    public Map<GridPosition, SymbolType> replaceCells(Set<GridPosition> positions) {
        Map<GridPosition, SymbolType> replacements = new LinkedHashMap<>();
        for (GridPosition position : positions) {
            SymbolType replacement = randomProvider.next();
            board[position.getRow()][position.getColumn()] = replacement;
            replacements.put(position, replacement);
        }
        return replacements;
    }

    public boolean hasSuperRerollCandidates() {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (isSuperRerollCandidate(board[row][column])) {
                    return true;
                }
            }
        }
        return false;
    }

    public Map<GridPosition, SymbolType> superReroll(SymbolReroller reroller) {
        if (reroller == null) {
            throw new IllegalArgumentException("reroller must not be null");
        }
        Map<GridPosition, SymbolType> rerolledCells = new LinkedHashMap<>();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                SymbolType currentSymbol = board[row][column];
                if (!isSuperRerollCandidate(currentSymbol)) {
                    continue;
                }
                SymbolType rerolledSymbol = reroller.reroll(currentSymbol);
                board[row][column] = rerolledSymbol;
                rerolledCells.put(new GridPosition(row, column), rerolledSymbol);
            }
        }
        return rerolledCells;
    }

    private boolean isSuperRerollCandidate(SymbolType symbol) {
        return symbol != null && !symbol.isScoring();
    }

    private void validateRow(int row) {
        if (row < 0 || row >= SIZE) {
            throw new IllegalArgumentException("Row out of range: " + row);
        }
    }

    private void validateColumn(int column) {
        if (column < 0 || column >= SIZE) {
            throw new IllegalArgumentException("Column out of range: " + column);
        }
    }

    private void validateMode(TestMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("mode must not be null");
        }
    }
}
