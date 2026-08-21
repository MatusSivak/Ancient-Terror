package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class GridBoardActor extends Group {
    private static final float SHIFT_DURATION = 0.24f;
    private static final float REROLL_HALF_DURATION = 0.10f;
    private static final float REFILL_DURATION = 0.64f;
    private static final float TOKEN_1_TO_3_IMPLOSION_END_SCALE = 1.5f;
    private static final float TOKEN_5_TO_6_IMPLOSION_END_SCALE = 0.65f;
    private static final float SWIPE_MIN_CELL_RATIO = 0.33f;
    private static final float BACKGROUND_SCALE = 1.3f;
    private static final float BACKGROUND_Y_OFFSET_PX = 0;
    private static final float OVERLAY_SCALE = 1.9f;
    private static final boolean SHOW_DEBUG_CLIP_BOUNDS = false;
    private static final float DEBUG_CLIP_BOUNDS_THICKNESS_PX = 3f;
    private static final float SYMBOL_GAP_PX = 45f;
    private static final float SYMBOL_SIZE_SCALE = 1f;
    private static final int[] PERIMETER_ROWS = {0, 0, 0, 1, 2, 2, 2, 1};
    private static final int[] PERIMETER_COLUMNS = {0, 1, 2, 2, 2, 1, 0, 0};

    public interface MoveSelectedListener {
        void onMoveSelected(GridMove move);
    }

    public interface SwapCompleteListener {
        void onSwapComplete(GridPosition pos1, GridPosition pos2);
    }

    public interface RerollTargetListener {
        void onRerollTargetSelected(GridPosition position);
    }

    private final GridTestController controller;
    private final GridTestAssets assets;
    private final Group boardLayer;
    private final Group spawnLayer;
    private final Group symbolLayer;
    private final Group implosionLayer;
    private final Container<Group> symbolClipContainer;
    private final Image backgroundImage;
    private final Image overlayImage;
    private final Actor debugClipBoundsActor;
    private final GridSymbolActor[][] symbolActors;
    private MoveSelectedListener moveSelectedListener;
    private SwapCompleteListener swapCompleteListener;
    private RerollTargetListener rerollTargetListener;
    private boolean interactionEnabled = true;
    private boolean rerollTargetingMode;
    private boolean swapSelectionMode = false;
    private GridPosition swapFirstSelection = null;
    private GridSymbolActor swapFirstSelectionActor = null;
    private float layoutScale = 1f;
    private float boardSize;
    private float cellSize;
    private float swipeStartX;
    private float swipeStartY;
    private int swipeStartRow = -1;
    private int swipeStartColumn = -1;
    private int swipePointer = -1;

    public GridBoardActor(GridTestController controller, GridTestAssets assets) {
        this.controller = controller;
        this.assets = assets;
        boardLayer = new Group();
        spawnLayer = new Group();
        symbolLayer = new Group();
        implosionLayer = new Group();
        symbolClipContainer = new Container<>(symbolLayer);
        symbolClipContainer.setClip(true);
        symbolClipContainer.fill();
        backgroundImage = new Image(new TextureRegionDrawable(assets.getBoardBackground()));
        overlayImage = assets.getOverlayRegion() == null ? null : new Image(new TextureRegionDrawable(assets.getOverlayRegion()));
        debugClipBoundsActor = SHOW_DEBUG_CLIP_BOUNDS
                ? new DebugBoundsActor(CustomAssetManager.getTextureRegion("background/pure_white.png"), DEBUG_CLIP_BOUNDS_THICKNESS_PX, Color.RED)
                : null;
        symbolActors = new GridSymbolActor[GridBoard.SIZE][GridBoard.SIZE];

        boardLayer.addActor(backgroundImage);
        boardLayer.addActor(spawnLayer);
        boardLayer.addActor(symbolClipContainer);
        boardLayer.addActor(implosionLayer);
        if (overlayImage != null) {
            boardLayer.addActor(overlayImage);
        }
        if (debugClipBoundsActor != null) {
            boardLayer.addActor(debugClipBoundsActor);
        }

        addActor(boardLayer);
        createBoardSymbols();
        addSwipeInput();
    }

    public void setMoveSelectedListener(MoveSelectedListener moveSelectedListener) {
        this.moveSelectedListener = moveSelectedListener;
    }

    public void setLayoutScale(float layoutScale) {
        this.layoutScale = layoutScale <= 0f ? 1f : layoutScale;
    }

    public void setInteractionEnabled(boolean enabled) {
        interactionEnabled = enabled;
        if (!enabled) {
            clearSwipeState();
        }
    }

    public void layout(float centerX, float centerY, float desiredBoardSize) {
        boardSize = desiredBoardSize;
        cellSize = boardSize / GridBoard.SIZE;
        float symbolClipPadding = symbolClipPadding();
        float boardX = centerX - boardSize / 2f;
        float boardY = centerY - boardSize / 2f;

        boardLayer.setBounds(boardX, boardY, boardSize, boardSize);
        spawnLayer.setBounds(0f, 0f, boardSize, boardSize);
        float backgroundSize = boardSize * BACKGROUND_SCALE;
        backgroundImage.setBounds(
                (boardSize - backgroundSize) / 2f,
                (boardSize - backgroundSize) / 2f + BACKGROUND_Y_OFFSET_PX,
                backgroundSize,
                backgroundSize
        );
        symbolClipContainer.setBounds(
                -symbolClipPadding,
                -symbolClipPadding,
                boardSize + symbolClipPadding * 2f,
                boardSize + symbolClipPadding * 2f
        );
        symbolLayer.setBounds(0f, 0f, boardSize + symbolClipPadding * 2f, boardSize + symbolClipPadding * 2f);
        implosionLayer.setBounds(0f, 0f, boardSize, boardSize);
        if (debugClipBoundsActor != null) {
            debugClipBoundsActor.setBounds(
                    -symbolClipPadding,
                    -symbolClipPadding,
                    boardSize + symbolClipPadding * 2f,
                    boardSize + symbolClipPadding * 2f
            );
        }
        if (overlayImage != null) {
            float overlayMaxSize = boardSize * OVERLAY_SCALE;
            float overlaySourceWidth = overlayImage.getDrawable().getMinWidth();
            float overlaySourceHeight = overlayImage.getDrawable().getMinHeight();
            float overlayWidth = overlayMaxSize;
            float overlayHeight = overlayMaxSize;
            if (overlaySourceWidth > 0f && overlaySourceHeight > 0f) {
                float overlayScale = Math.min(overlayMaxSize / overlaySourceWidth, overlayMaxSize / overlaySourceHeight);
                overlayWidth = overlaySourceWidth * overlayScale;
                overlayHeight = overlaySourceHeight * overlayScale;
            }
            overlayImage.setBounds(
                    (boardSize - overlayWidth) / 2f,
                    (boardSize - overlayHeight) / 2f - 20,
                    overlayWidth,
                    overlayHeight
            );
        }

        layoutSymbolActors();
    }

    public void syncBoardToActors() {
        rebuildSymbolLayerChildren();
        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                GridSymbolActor actor = symbolActors[row][column];
                actor.setSymbolType(assets, controller.getBoard().getCell(row, column));
                layoutActor(actor, row, column);
            }
        }
    }

    public void resetAnimations() {
        exitRerollTargetingMode();
        exitSwapSelectionMode();
        clearActions();
        boardLayer.clearActions();
        spawnLayer.clearActions();
        spawnLayer.clearChildren();
        symbolLayer.clearActions();
        rebuildSymbolLayerChildren();
        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                symbolActors[row][column].clearActions();
            }
        }
        clearSwipeState();
        layoutSymbolActors();
    }

    public void animateShift(GridShiftOutcome outcome, Runnable onComplete) {
        int lineIndex = outcome.getMove().getIndex();
        switch (outcome.getMove().getType()) {
            case ROW_LEFT:
                animateRowLeft(lineIndex, outcome.getIncomingSymbol(), onComplete);
                return;
            case ROW_RIGHT:
                animateRowRight(lineIndex, outcome.getIncomingSymbol(), onComplete);
                return;
            case COLUMN_UP:
                animateColumnUp(lineIndex, outcome.getIncomingSymbol(), onComplete);
                return;
            case COLUMN_DOWN:
                animateColumnDown(lineIndex, outcome.getIncomingSymbol(), onComplete);
                return;
            default:
                throw new IllegalArgumentException("Unsupported move type " + outcome.getMove().getType());
        }
    }

    public void animateMatchWave(List<GridMatch> matches, Map<GridPosition, SymbolType> replacements, Runnable onComplete) {
        // Build a position→symbol map from the matches themselves, not from the live board.
        // resolveMatches() is called before animateMatchWave, so the board already holds
        // replacement symbols by this point.
        Map<GridPosition, SymbolType> cellSymbols = new java.util.LinkedHashMap<>();
        Set<GridPosition> uniqueCells = new LinkedHashSet<>();
        for (GridMatch match : matches) {
            for (GridPosition pos : match.getCells()) {
                cellSymbols.putIfAbsent(pos, match.getSymbol());
                uniqueCells.add(pos);
            }
        }

        List<GridSymbolActor> matchedActors = new ArrayList<>(uniqueCells.size());
        for (GridPosition position : uniqueCells) {
            matchedActors.add(symbolActors[position.getRow()][position.getColumn()]);
        }
        if (matchedActors.isEmpty()) {
            onComplete.run();
            return;
        }

        // Hide the underlying symbol immediately — the implosion effect is the foreground visual.
        for (GridSymbolActor actor : matchedActors) {
            actor.clearActions();
            actor.setScale(1f);
            actor.getColor().a = 0f;
        }

        final int[] finishedCount = {0};
        for (GridPosition position : uniqueCells) {
            TokenLayout layout = tokenLayout(position.getRow(), position.getColumn());
            float centerX = symbolClipContainer.getX() + layout.x + layout.width / 2f;
            float centerY = symbolClipContainer.getY() + layout.y + layout.height / 2f;
            SymbolType symbol = cellSymbols.get(position);
            ImplosionActor implosionActor = new ImplosionActor(
                    assets.getImplosionFrames(symbol), overlayFrames(symbol), centerX, centerY, layout.width,
                    implosionEndScale(symbol), destroyAnimationStartAlpha(symbol), destroyAnimationEndAlpha(symbol), overlayStartAlpha(symbol), overlayEndAlpha(symbol),
                    null,
                    () -> {
                        finishedCount[0]++;
                        if (finishedCount[0] == uniqueCells.size()) {
                            animateRefillWave(matchedActors, replacements, onComplete);
                        }
                    }
            );
            implosionLayer.addActor(implosionActor);
        }
    }

    public void animateSuperReroll(Map<GridPosition, SymbolType> rerolledCells, Runnable onComplete) {
        if (rerolledCells == null || rerolledCells.isEmpty()) {
            onComplete.run();
            return;
        }

        List<GridSymbolActor> rerolledActors = new ArrayList<>(rerolledCells.size());
        Map<GridPosition, SymbolType> previousSymbols = new java.util.LinkedHashMap<>();
        for (Map.Entry<GridPosition, SymbolType> rerolledCell : rerolledCells.entrySet()) {
            GridPosition position = rerolledCell.getKey();
            GridSymbolActor actor = symbolActors[position.getRow()][position.getColumn()];
            rerolledActors.add(actor);
            previousSymbols.put(position, actor.getSymbolType());
            actor.clearActions();
            actor.setScale(1f);
            actor.getColor().a = 0f;
        }

        final int[] finishedCount = {0};
        for (Map.Entry<GridPosition, SymbolType> rerolledCell : rerolledCells.entrySet()) {
            GridPosition position = rerolledCell.getKey();
            TokenLayout layout = tokenLayout(position.getRow(), position.getColumn());
            float centerX = symbolClipContainer.getX() + layout.x + layout.width / 2f;
            float centerY = symbolClipContainer.getY() + layout.y + layout.height / 2f;
            SymbolType previousSymbol = previousSymbols.get(position);
            ImplosionActor implosionActor = new ImplosionActor(
                    assets.getImplosionFrames(previousSymbol),
                    overlayFrames(previousSymbol),
                    centerX,
                    centerY,
                    layout.width,
                    implosionEndScale(previousSymbol),
                    destroyAnimationStartAlpha(previousSymbol),
                    destroyAnimationEndAlpha(previousSymbol),
                    overlayStartAlpha(previousSymbol),
                    overlayEndAlpha(previousSymbol),
                    null,
                    () -> {
                        finishedCount[0]++;
                        if (finishedCount[0] == rerolledCells.size()) {
                            animateRefillWave(rerolledActors, rerolledCells, onComplete);
                        }
                    }
            );
            implosionLayer.addActor(implosionActor);
        }
    }

    public void animateReroll(GridPosition position, SymbolType rerolledSymbol, Runnable onComplete) {
        GridSymbolActor actor = symbolActors[position.getRow()][position.getColumn()];
        actor.clearActions();
        actor.setOrigin(actor.getWidth() / 2f, actor.getHeight() / 2f);
        actor.addAction(new FastForwardAction<>(sequence(
                parallel(
                        scaleTo(0.65f, 0.65f, REROLL_HALF_DURATION, Interpolation.sineIn),
                        alpha(0.2f, REROLL_HALF_DURATION, Interpolation.sineIn),
                        Actions.rotateBy(90f, REROLL_HALF_DURATION, Interpolation.sineIn)
                ),
                run(() -> actor.setSymbolType(assets, rerolledSymbol)),
                parallel(
                        scaleTo(1f, 1f, REROLL_HALF_DURATION, Interpolation.sineOut),
                        alpha(1f, REROLL_HALF_DURATION, Interpolation.sineOut),
                        Actions.rotateBy(90f, REROLL_HALF_DURATION, Interpolation.sineOut)
                ),
                run(() -> {
                    actor.setRotation(0f);
                    onComplete.run();
                })
        )));
    }

    private void animateRefillWave(List<GridSymbolActor> matchedActors, Map<GridPosition, SymbolType> replacements, Runnable onComplete) {
        final int totalEffects = matchedActors.size() + replacements.size();
        final int[] completedEffects = {0};
        Runnable effectFinished = () -> {
            completedEffects[0]++;
            if (completedEffects[0] == totalEffects) {
                onComplete.run();
            }
        };

        for (Map.Entry<GridPosition, SymbolType> replacement : replacements.entrySet()) {
            GridPosition position = replacement.getKey();
            GridSymbolActor actor = symbolActors[position.getRow()][position.getColumn()];
            actor.setSymbolType(assets, replacement.getValue());
            actor.setScale(0.7f);
            actor.getColor().a = 0f;

            TokenLayout layout = tokenLayout(position.getRow(), position.getColumn());
            float centerX = symbolClipContainer.getX() + layout.x + layout.width / 2f;
            float centerY = symbolClipContainer.getY() + layout.y + layout.height / 2f;
            ImplosionActor spawnActor = new ImplosionActor(
                    assets.getSpawnFrames(),
                    null,
                    centerX,
                    centerY,
                    layout.width,
                    1.5f,
                    1f,
                    1f,
                    ImplosionAnimation.DEFAULT_OVERLAY_START_ALPHA,
                    ImplosionAnimation.DEFAULT_OVERLAY_END_ALPHA,
                    null,
                    effectFinished
            );
            spawnLayer.addActor(spawnActor);
        }

        for (GridSymbolActor actor : matchedActors) {
            actor.clearActions();
            actor.addAction(new FastForwardAction<>(sequence(
                    parallel(
                            scaleTo(1f, 1f, REFILL_DURATION, Interpolation.swingOut),
                            alpha(1f, REFILL_DURATION, Interpolation.sineOut)
                    ),
                    run(effectFinished)
            )));
        }
    }

    private void animateRowLeft(int row, SymbolType incoming, Runnable onComplete) {
        GridSymbolActor outgoing = symbolActors[row][0];
        GridSymbolActor second = symbolActors[row][1];
        GridSymbolActor third = symbolActors[row][2];

        TokenLayout layout0 = tokenLayout(row, 0);
        TokenLayout layout1 = tokenLayout(row, 1);
        TokenLayout layout2 = tokenLayout(row, 2);
        float horizontalStep = layout1.x - layout0.x;
        TokenLayout outgoingLayout = new TokenLayout(layout0.x - horizontalStep, layout0.y, layout0.width, layout0.height);

        GridSymbolActor incomingActor = createIncomingActor(incoming, row, 2, layout2.x + horizontalStep, layout2.y);
        symbolActors[row][0] = second;
        symbolActors[row][1] = third;
        symbolActors[row][2] = incomingActor;

        animateLineMove(
                new GridSymbolActor[] {outgoing, second, third, incomingActor},
                new TokenLayout[] {outgoingLayout, layout0, layout1, layout2},
                () -> finishShiftAnimation(outgoing, onComplete)
        );
    }

    private void animateRowRight(int row, SymbolType incoming, Runnable onComplete) {
        GridSymbolActor outgoing = symbolActors[row][2];
        GridSymbolActor first = symbolActors[row][0];
        GridSymbolActor second = symbolActors[row][1];

        TokenLayout layout0 = tokenLayout(row, 0);
        TokenLayout layout1 = tokenLayout(row, 1);
        TokenLayout layout2 = tokenLayout(row, 2);
        float horizontalStep = layout1.x - layout0.x;
        TokenLayout outgoingLayout = new TokenLayout(layout2.x + horizontalStep, layout2.y, layout2.width, layout2.height);

        GridSymbolActor incomingActor = createIncomingActor(incoming, row, 0, layout0.x - horizontalStep, layout0.y);
        symbolActors[row][0] = incomingActor;
        symbolActors[row][1] = first;
        symbolActors[row][2] = second;

        animateLineMove(
                new GridSymbolActor[] {outgoing, first, second, incomingActor},
                new TokenLayout[] {outgoingLayout, layout1, layout2, layout0},
                () -> finishShiftAnimation(outgoing, onComplete)
        );
    }

    private void animateColumnUp(int column, SymbolType incoming, Runnable onComplete) {
        GridSymbolActor outgoing = symbolActors[0][column];
        GridSymbolActor middle = symbolActors[1][column];
        GridSymbolActor bottom = symbolActors[2][column];

        TokenLayout layout0 = tokenLayout(0, column);
        TokenLayout layout1 = tokenLayout(1, column);
        TokenLayout layout2 = tokenLayout(2, column);
        float topVerticalStep = layout0.y - layout1.y;
        float bottomVerticalStep = layout1.y - layout2.y;
        TokenLayout outgoingLayout = new TokenLayout(layout0.x, layout0.y + topVerticalStep, layout0.width, layout0.height);

        GridSymbolActor incomingActor = createIncomingActor(incoming, 2, column, layout2.x, layout2.y - bottomVerticalStep);
        symbolActors[0][column] = middle;
        symbolActors[1][column] = bottom;
        symbolActors[2][column] = incomingActor;

        animateLineMove(
                new GridSymbolActor[] {outgoing, middle, bottom, incomingActor},
                new TokenLayout[] {outgoingLayout, layout0, layout1, layout2},
                () -> finishShiftAnimation(outgoing, onComplete)
        );
    }

    private void animateColumnDown(int column, SymbolType incoming, Runnable onComplete) {
        GridSymbolActor outgoing = symbolActors[2][column];
        GridSymbolActor top = symbolActors[0][column];
        GridSymbolActor middle = symbolActors[1][column];

        TokenLayout layout0 = tokenLayout(0, column);
        TokenLayout layout1 = tokenLayout(1, column);
        TokenLayout layout2 = tokenLayout(2, column);
        float topVerticalStep = layout0.y - layout1.y;
        float bottomVerticalStep = layout1.y - layout2.y;
        TokenLayout outgoingLayout = new TokenLayout(layout2.x, layout2.y - bottomVerticalStep, layout2.width, layout2.height);

        GridSymbolActor incomingActor = createIncomingActor(incoming, 0, column, layout0.x, layout0.y + topVerticalStep);
        symbolActors[0][column] = incomingActor;
        symbolActors[1][column] = top;
        symbolActors[2][column] = middle;

        animateLineMove(
                new GridSymbolActor[] {outgoing, top, middle, incomingActor},
                new TokenLayout[] {outgoingLayout, layout1, layout2, layout0},
                () -> finishShiftAnimation(outgoing, onComplete)
        );
    }

    private void animateLineMove(GridSymbolActor[] actors, TokenLayout[] targets, Runnable onComplete) {
        final int[] completed = {0};
        for (int i = 0; i < actors.length; i++) {
            GridSymbolActor actor = actors[i];
            TokenLayout target = targets[i];
            actor.clearActions();
            actor.addAction(new FastForwardAction<>(sequence(
                    parallel(
                            Actions.moveTo(target.x, target.y, SHIFT_DURATION, Interpolation.sine),
                            Actions.sizeTo(target.width, target.height, SHIFT_DURATION, Interpolation.sine)
                    ),
                    run(() -> {
                        completed[0]++;
                        if (completed[0] == actors.length) {
                            snapActorsToGrid();
                            onComplete.run();
                        }
                    })
            )));
        }
    }

    private void createBoardSymbols() {
        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                GridSymbolActor actor = new GridSymbolActor(assets, controller.getBoard().getCell(row, column));
                symbolActors[row][column] = actor;
            }
        }
        rebuildSymbolLayerChildren();
    }

    public void enterSwapSelectionMode(SwapCompleteListener listener) {
        this.swapCompleteListener = listener;
        swapSelectionMode = true;
        swapFirstSelection = null;
        clearSwipeState();
    }

    public void enterRerollTargetingMode(RerollTargetListener listener) {
        rerollTargetListener = listener;
        rerollTargetingMode = true;
        clearSwipeState();
        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                if (!controller.getBoard().isGap(new GridPosition(row, column))) {
                    highlightToken(symbolActors[row][column]);
                }
            }
        }
    }

    public void exitRerollTargetingMode() {
        if (!rerollTargetingMode) {
            return;
        }
        rerollTargetingMode = false;
        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                unhighlightToken(symbolActors[row][column]);
            }
        }
        clearSwipeState();
    }

    public void exitSwapSelectionMode() {
        if (swapFirstSelectionActor != null) {
            unhighlightToken(swapFirstSelectionActor);
        }
        swapSelectionMode = false;
        swapFirstSelection = null;
        swapFirstSelectionActor = null;
        clearSwipeState();
    }

    public void animateSwap(GridPosition pos1, GridPosition pos2, Runnable onComplete) {
        System.out.println("SWAP_ANIM: animateSwap starting: (" + pos1.getRow() + "," + pos1.getColumn() + ") <-> (" + pos2.getRow() + "," + pos2.getColumn() + ")");
        GridSymbolActor actor1 = symbolActors[pos1.getRow()][pos1.getColumn()];
        GridSymbolActor actor2 = symbolActors[pos2.getRow()][pos2.getColumn()];

        TokenLayout layout1 = tokenLayout(pos1.getRow(), pos1.getColumn());
        TokenLayout layout2 = tokenLayout(pos2.getRow(), pos2.getColumn());

        final int[] completed = {0};
        Runnable swapFinished = () -> {
            completed[0]++;
            System.out.println("SWAP_ANIM: Actor animation complete: " + completed[0] + "/2");
            if (completed[0] == 2) {
                System.out.println("SWAP_ANIM: Both animations complete, snapping to grid");
                snapActorsToGrid();
                System.out.println("SWAP_ANIM: Calling onComplete");
                onComplete.run();
            }
        };

        actor1.clearActions();
        actor1.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(
                        Actions.moveTo(layout2.x, layout2.y, SHIFT_DURATION, Interpolation.sineOut)
                ),
                Actions.run(swapFinished)
        )));

        actor2.clearActions();
        actor2.addAction(new FastForwardAction<>(Actions.sequence(
                Actions.parallel(
                        Actions.moveTo(layout1.x, layout1.y, SHIFT_DURATION, Interpolation.sineOut)
                ),
                Actions.run(swapFinished)
        )));

        symbolActors[pos1.getRow()][pos1.getColumn()] = actor2;
        symbolActors[pos2.getRow()][pos2.getColumn()] = actor1;
        System.out.println("SWAP_ANIM: symbolActors array updated");
    }

    public void animateSpin(Runnable onComplete) {
        int perimeterSize = PERIMETER_ROWS.length;
        GridSymbolActor[] actors = new GridSymbolActor[perimeterSize];
        TokenLayout[] targets = new TokenLayout[perimeterSize];
        for (int i = 0; i < perimeterSize; i++) {
            actors[i] = symbolActors[PERIMETER_ROWS[i]][PERIMETER_COLUMNS[i]];
        }
        for (int i = 0; i < perimeterSize; i++) {
            int target = (i + 1) % perimeterSize;
            int targetRow = PERIMETER_ROWS[target];
            int targetColumn = PERIMETER_COLUMNS[target];
            targets[i] = tokenLayout(targetRow, targetColumn);
            symbolActors[targetRow][targetColumn] = actors[i];
        }

        animateLineMove(actors, targets, onComplete);
    }

    private void addSwipeInput() {
        boardLayer.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!interactionEnabled || cellSize <= 0f || !isInsideBoard(x, y)) {
                    return false;
                }
                
                int row = toRow(y);
                int col = toColumn(x);

                if (rerollTargetingMode) {
                    GridPosition position = new GridPosition(row, col);
                    if (controller.getBoard().isGap(position)) {
                        return true;
                    }
                    exitRerollTargetingMode();
                    if (rerollTargetListener != null) {
                        rerollTargetListener.onRerollTargetSelected(position);
                    }
                    return true;
                }

                if (swapSelectionMode) {
                    swipePointer = pointer;  // Track pointer during swap selection
                    GridPosition currentPos = new GridPosition(row, col);
                    GridSymbolActor currentActor = symbolActors[row][col];
                    
                    if (swapFirstSelection == null) {
                        swapFirstSelection = currentPos;
                        swapFirstSelectionActor = currentActor;
                        highlightToken(currentActor);
                        Gdx.app.log("SWAP", "First token selected at (" + row + ", " + col + ")");
                        return true;
                    } else if (swapFirstSelection.equals(currentPos)) {
                        // Deselect
                        Gdx.app.log("SWAP", "First token deselected at (" + row + ", " + col + ")");
                        unhighlightToken(swapFirstSelectionActor);
                        swapFirstSelection = null;
                        swapFirstSelectionActor = null;
                        return true;
                    } else if (isOrthogonallyAdjacent(swapFirstSelection, currentPos)) {
                        // Valid swap - save positions BEFORE clearing selection
                        Gdx.app.log("SWAP", "Second token selected at (" + row + ", " + col + ") - valid adjacent pair");
                        unhighlightToken(swapFirstSelectionActor);
                        GridPosition pos1 = swapFirstSelection;
                        GridPosition pos2 = currentPos;
                        exitSwapSelectionMode();
                        if (swapCompleteListener != null) {
                            swapCompleteListener.onSwapComplete(pos1, pos2);
                        }
                        return true;
                    } else {
                        // Invalid selection, try new first selection
                        Gdx.app.log("SWAP", "Non-adjacent token at (" + row + ", " + col + ") - changing first selection");
                        unhighlightToken(swapFirstSelectionActor);
                        swapFirstSelection = currentPos;
                        swapFirstSelectionActor = currentActor;
                        highlightToken(currentActor);
                        return true;
                    }
                }
                
                swipePointer = pointer;
                swipeStartX = x;
                swipeStartY = y;
                swipeStartColumn = col;
                swipeStartRow = row;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!interactionEnabled) {
                    return;
                }
                
                if (rerollTargetingMode || swapSelectionMode) {
                    // Don't process swipe during swap selection
                    if (pointer == swipePointer) {
                        swipePointer = -1;
                    }
                    return;
                }
                
                if (pointer != swipePointer) {
                    return;
                }
                
                swipePointer = -1;

                float dx = x - swipeStartX;
                float dy = y - swipeStartY;
                float minSwipeDistance = cellSize * SWIPE_MIN_CELL_RATIO;
                if (Math.abs(dx) < minSwipeDistance && Math.abs(dy) < minSwipeDistance) {
                    return;
                }

                GridMove move = toSwipeMove(dx, dy);
                if (moveSelectedListener != null && move != null) {
                    moveSelectedListener.onMoveSelected(move);
                }
            }
        });
    }

    private GridMove toSwipeMove(float dx, float dy) {
        if (Math.abs(dx) >= Math.abs(dy)) {
            if (swipeStartRow < 0 || swipeStartRow >= GridBoard.SIZE) {
                return null;
            }
            return dx < 0 ? new GridMove(GridMoveType.ROW_LEFT, swipeStartRow) : new GridMove(GridMoveType.ROW_RIGHT, swipeStartRow);
        }

        if (swipeStartColumn < 0 || swipeStartColumn >= GridBoard.SIZE) {
            return null;
        }
        return dy > 0 ? new GridMove(GridMoveType.COLUMN_UP, swipeStartColumn) : new GridMove(GridMoveType.COLUMN_DOWN, swipeStartColumn);
    }

    private boolean isInsideBoard(float x, float y) {
        return x >= 0f && x <= boardSize && y >= 0f && y <= boardSize;
    }

    private int toColumn(float x) {
        int column = (int) (x / cellSize);
        return Math.max(0, Math.min(GridBoard.SIZE - 1, column));
    }

    private int toRow(float y) {
        int fromBottom = (int) (y / cellSize);
        int row = GridBoard.SIZE - 1 - fromBottom;
        return Math.max(0, Math.min(GridBoard.SIZE - 1, row));
    }

    private boolean isOrthogonallyAdjacent(GridPosition pos1, GridPosition pos2) {
        int rowDiff = Math.abs(pos1.getRow() - pos2.getRow());
        int colDiff = Math.abs(pos1.getColumn() - pos2.getColumn());
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    private Vector2 cellPosition(int row, int column) {
        return new Vector2(column * cellSize, (GridBoard.SIZE - 1 - row) * cellSize);
    }

    private Vector2 tokenPosition(int row, int column) {
        float tokenSize = tokenSize();
        float symbolClipPadding = symbolClipPadding();
        float halfGap = scaledSymbolGap() / 2f;
        float xOffset = (column - 1) * halfGap;
        float yOffset = (1 - row) * halfGap;
        float centerX = column * cellSize + cellSize / 2f;
        float centerY = (GridBoard.SIZE - row - 0.5f) * cellSize;
        return new Vector2(
                symbolClipPadding + centerX - tokenSize / 2f + xOffset,
                symbolClipPadding + centerY - tokenSize / 2f + yOffset
        );
    }

    private void layoutSymbolActors() {
        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                layoutActor(symbolActors[row][column], row, column);
            }
        }
    }

    private void layoutActor(GridSymbolActor actor, int row, int column) {
        TokenLayout layout = tokenLayout(row, column);
        actor.setBounds(layout.x, layout.y, layout.width, layout.height);
        actor.setOrigin(actor.getWidth() / 2f, actor.getHeight() / 2f);
        actor.setScale(1f);
        actor.getColor().a = 1f;
    }

    private TokenLayout tokenLayout(int row, int column) {
        Vector2 position = tokenPosition(row, column);
        float tokenSize = tokenSize();
        return new TokenLayout(position.x, position.y, tokenSize, tokenSize);
    }

    private float tokenSize() {
        return tokenSize(cellSize);
    }

    private float symbolClipPadding() {
        return scaledSymbolGap() + (cellSize - tokenSize(cellSize)) / 2f;
    }

    private float scaledSymbolGap() {
        return SYMBOL_GAP_PX * layoutScale;
    }

    static float tokenSize(float cellSize) {
        return cellSize * SYMBOL_SIZE_SCALE;
    }

    private static float implosionEndScale(SymbolType symbol) {
        switch (symbol) {
            case ONE:
            case TWO:
            case THREE:
            case FOUR:
                return TOKEN_1_TO_3_IMPLOSION_END_SCALE;
            case FIVE:
            case SIX:
                return TOKEN_5_TO_6_IMPLOSION_END_SCALE;
            default:
                return ImplosionAnimation.DEFAULT_END_SCALE;
        }
    }

    private Array<TextureRegion> overlayFrames(SymbolType symbol) {
        switch (symbol) {
            case ONE:
            case TWO:
            case THREE:
            case FOUR:
                return assets.getExplosionOverlayFrames();
            case FIVE:
            case SIX:
                return assets.getImplosionOverlayFrames();
            default:
                return null;
        }
    }

    private static float overlayStartAlpha(SymbolType symbol) {
        switch (symbol) {
            case FIVE:
            case SIX:
                return 1f;
            default:
                return ImplosionAnimation.DEFAULT_OVERLAY_START_ALPHA;
        }
    }

    private static float overlayEndAlpha(SymbolType symbol) {
        switch (symbol) {
            case FIVE:
            case SIX:
                return 1f;
            default:
                return ImplosionAnimation.DEFAULT_OVERLAY_END_ALPHA;
        }
    }

    private static float destroyAnimationStartAlpha(SymbolType symbol) {
        switch (symbol) {
            case ONE:
            case TWO:
            case THREE:
            case FOUR:
                return 1f;
            default:
                return 1f;
        }
    }

    private static float destroyAnimationEndAlpha(SymbolType symbol) {
        switch (symbol) {
            case ONE:
            case TWO:
            case THREE:
            case FOUR:
                return 0f;
            default:
                return 1f;
        }
    }

    static float symbolClipPadding(float cellSize) {
        return SYMBOL_GAP_PX + (cellSize - tokenSize(cellSize)) / 2f;
    }

    private void snapActorsToGrid() {
        layoutSymbolActors();
    }

    private GridSymbolActor createIncomingActor(SymbolType incomingSymbol, int row, int column, float startX, float startY) {
        GridSymbolActor incomingActor = new GridSymbolActor(assets, incomingSymbol);
        layoutActor(incomingActor, row, column);
        incomingActor.setPosition(startX, startY);
        symbolLayer.addActor(incomingActor);
        return incomingActor;
    }

    private void finishShiftAnimation(GridSymbolActor outgoingActor, Runnable onComplete) {
        outgoingActor.remove();
        snapActorsToGrid();
        onComplete.run();
    }

    private void rebuildSymbolLayerChildren() {
        symbolLayer.clearChildren();
        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                symbolLayer.addActor(symbolActors[row][column]);
            }
        }
    }

    private static class TokenLayout {
        private final float x;
        private final float y;
        private final float width;
        private final float height;

        private TokenLayout(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private static class ImplosionActor extends Actor {
        private final ImplosionAnimation animation;
        private final Runnable onFinished;
        private boolean completionFired = false;

        ImplosionActor(com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> frames,
                       com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> overlayFrames,
                       float centerX, float centerY, float size, float endScale,
                       float startAlpha, float endAlpha,
                       float overlayStartAlpha, float overlayEndAlpha,
                       Interpolation scaleInterpolation, Runnable onFinished) {
            this.animation = new ImplosionAnimation(
                    frames, overlayFrames, centerX, centerY, size, endScale, startAlpha, endAlpha, overlayStartAlpha, overlayEndAlpha, scaleInterpolation);
            this.onFinished = onFinished;
            setTouchable(Touchable.disabled);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            animation.update(delta);
            if (animation.isFinished() && !completionFired) {
                completionFired = true;
                remove();
                onFinished.run();
            }
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            // Matched symbol actors have alpha=0, which leaks into the SpriteBatch color.
            // Reset to white before drawing so the implosion is fully visible.
            batch.setColor(1f, 1f, 1f, parentAlpha);
            animation.draw(batch, parentAlpha);
        }
    }

    private static class DebugBoundsActor extends Actor {
        private final TextureRegion pixel;
        private final float lineThickness;
        private final Color color;

        private DebugBoundsActor(TextureRegion pixel, float lineThickness, Color color) {
            this.pixel = pixel;
            this.lineThickness = lineThickness;
            this.color = new Color(color);
            setTouchable(Touchable.disabled);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color previousColor = batch.getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(pixel, getX(), getY(), getWidth(), lineThickness);
            batch.draw(pixel, getX(), getTop() - lineThickness, getWidth(), lineThickness);
            batch.draw(pixel, getX(), getY(), lineThickness, getHeight());
            batch.draw(pixel, getRight() - lineThickness, getY(), lineThickness, getHeight());
            batch.setColor(previousColor);
        }
    }

    private void clearSwipeState() {
        swipePointer = -1;
        swipeStartRow = -1;
        swipeStartColumn = -1;
    }

    private void highlightToken(GridSymbolActor actor) {
        if (actor == null) return;
        // Use setColor to tint the token slightly brighter
        actor.setColor(1.3f, 1.3f, 1.3f, 1f);
    }

    private void unhighlightToken(GridSymbolActor actor) {
        if (actor == null) return;
        // Reset to normal color
        actor.setColor(1f, 1f, 1f, 1f);
    }
}
