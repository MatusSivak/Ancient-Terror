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
        boolean hasMatches = true;
        while (hasMatches) {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    board[row][col] = randomProvider.next();
                }
            }
            hasMatches = !findMatches().isEmpty();
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

    public List<GridMatch> findMatches() {
        List<GridMatch> matches = new ArrayList<>();
        
        // Check rows for matches
        for (int row = 0; row < SIZE; row++) {
            if (isThreeOfAKind(board[row][0], board[row][1], board[row][2])) {
                List<GridPosition> cells = new ArrayList<>(SIZE);
                for (int col = 0; col < SIZE; col++) {
                    cells.add(new GridPosition(row, col));
                }
                matches.add(new GridMatch(board[row][0], cells));
            }
        }
        
        // Check columns for matches
        for (int column = 0; column < SIZE; column++) {
            if (isThreeOfAKind(board[0][column], board[1][column], board[2][column])) {
                List<GridPosition> cells = new ArrayList<>(SIZE);
                for (int row = 0; row < SIZE; row++) {
                    cells.add(new GridPosition(row, column));
                }
                matches.add(new GridMatch(board[0][column], cells));
            }
        }
        
        return matches;
    }

    private boolean isThreeOfAKind(SymbolType first, SymbolType second, SymbolType third) {
        return first == second && first == third;
    }

    public boolean hasMatches() {
        return !findMatches().isEmpty();
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
}
