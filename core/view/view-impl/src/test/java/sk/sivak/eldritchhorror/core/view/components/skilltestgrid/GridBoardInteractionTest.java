package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class GridBoardInteractionTest {
    private static final SymbolType[] STABLE_BOARD = {
            SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
            SymbolType.FOUR, SymbolType.FIVE, SymbolType.SIX,
            SymbolType.TWO, SymbolType.THREE, SymbolType.ONE
    };
    private Application previousApplication;
    private Graphics previousGraphics;
    private Stage stage;
    private final PointerViewport viewport = new PointerViewport();
    private GridTestController controller;
    private GridBoardActor actor;
    private final List<GridPosition> taps = new ArrayList<>();
    private final List<GridPosition> selections = new ArrayList<>();
    private final List<GridMove> moves = new ArrayList<>();
    private int invalidTargets;
    private int completedSwaps;

    @Before
    public void setUp() {
        previousApplication = Gdx.app;
        previousGraphics = Gdx.graphics;
        Gdx.app = (Application) Proxy.newProxyInstance(Application.class.getClassLoader(),
                new Class<?>[] {Application.class}, (proxy, method, args) -> {
                    if ("log".equals(method.getName())) {
                        return null;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        Gdx.graphics = (Graphics) Proxy.newProxyInstance(Graphics.class.getClassLoader(),
                new Class<?>[] {Graphics.class}, (proxy, method, args) -> {
                    if ("getWidth".equals(method.getName()) || "getHeight".equals(method.getName())) {
                        return 1000;
                    }
                    if ("requestRendering".equals(method.getName())) {
                        return null;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(),
                new Class<?>[] {Batch.class}, (proxy, method, args) -> {
                    throw new UnsupportedOperationException(method.getName());
                });
        stage = new Stage(viewport, batch);
        controller = new GridTestController(new GridBoard(new SymbolRandomProvider() {
            private int index;

            @Override
            public SymbolType peekNext() {
                return STABLE_BOARD[index % STABLE_BOARD.length];
            }

            @Override
            public SymbolType next() {
                SymbolType next = peekNext();
                index++;
                return next;
            }
        }));
        controller.startTest(4);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        actor = new GridBoardActor(controller, new StubAssets(), action -> action);
        stage.addActor(actor);
        actor.layout(150f, 150f, 300f);
        actor.setTokenTappedListener(position -> {
            taps.add(position);
            if (controller.beginSwapSelection(position)) {
                actor.enterSwapSelectionMode(position, (first, second) -> {
                    if (first == null && second == null) {
                        assertTrue(controller.cancelSwapSelection());
                    } else {
                        controller.performSwap(first, second);
                        actor.setInteractionEnabled(false);
                        actor.animateSwap(first, second, () -> {
                            completedSwaps++;
                            controller.setState(GridTestState.WAITING_FOR_INPUT);
                            actor.setInteractionEnabled(true);
                        });
                    }
                });
            } else if (controller.getSwapRemaining() == 0) {
                actor.showExhaustedSwap(position);
            }
        });
        actor.setSwapTokenSelectedListener(selections::add);
        actor.setInvalidTargetListener(() -> invalidTargets++);
        actor.setMoveSelectedListener(move -> {
            moves.add(move);
            if (controller.canAcceptInput()) {
                controller.applyMove(move);
            } else if (controller.getMovesRemaining() == 0) {
                actor.showExhaustedShift(move);
            }
        });
    }

    @After
    public void tearDown() {
        try {
            if (stage != null) {
                stage.dispose();
            }
        } finally {
            Gdx.app = previousApplication;
            Gdx.graphics = previousGraphics;
        }
    }

    @Test
    public void renderedCellCornersAreClickableAtDifferentSizesAndScales() {
        actor.setTokenTappedListener(taps::add);
        for (float scale : new float[] {0.5f, 1f, 1.5f}) {
            for (float size : new float[] {60f, 300f}) {
                actor.setLayoutScale(scale);
                actor.layout(size / 2f + 37f, size / 2f + 61f, size);
                List<GridSymbolActor> symbols = descendants(actor, GridSymbolActor.class);
                float pitch = size / 3f + 45f * scale / 2f;
                for (int row = 0; row < 3; row++) {
                    for (int column = 0; column < 3; column++) {
                        GridSymbolActor symbol = symbols.get(row * 3 + column);
                        Vector2 center = symbol.localToStageCoordinates(
                                new Vector2(symbol.getWidth() / 2f, symbol.getHeight() / 2f));
                        for (float dx : new float[] {-pitch / 2f + 0.01f, pitch / 2f - 0.01f}) {
                            for (float dy : new float[] {-pitch / 2f + 0.01f, pitch / 2f - 0.01f}) {
                                int before = taps.size();
                                tap(center.x + dx, center.y + dy);
                                assertEquals(before + 1, taps.size());
                                assertEquals(new GridPosition(row, column), taps.get(before));
                            }
                        }
                    }
                }
            }
        }
        assertTrue(moves.isEmpty());
    }

    @Test
    public void visualBoundsRejectOutsidePointsAndAssignSharedBordersConsistently() {
        actor.setTokenTappedListener(taps::add);
        float origin = -33.75f;
        float pitch = 122.5f;
        tap(origin, origin);
        assertEquals(new GridPosition(2, 0), taps.get(0));
        tap(origin + pitch, origin + pitch);
        assertEquals(new GridPosition(1, 1), taps.get(1));
        tap(origin + pitch * 2, origin + pitch * 2);
        assertEquals(new GridPosition(0, 2), taps.get(2));
        tap(origin - 0.01f, 150f);
        tap(150f, origin - 0.01f);
        tap(origin + pitch * 3, 150f);
        tap(150f, origin + pitch * 3);
        assertEquals(3, taps.size());
        assertTrue(moves.isEmpty());
    }

    @Test
    public void deselectRestoresAbilitiesAndRerollTargetingWorksImmediately() {
        tap(27.5f, 272.5f);
        assertEquals(GridTestState.SWAP_SELECTING, controller.getState());
        assertFalse(controller.canActivateReroll());
        tap(27.5f, 272.5f);
        assertEquals(GridTestState.WAITING_FOR_INPUT, controller.getState());
        assertEquals(3, controller.getSwapRemaining());
        assertTrue(controller.canActivateReroll());
        assertTrue(controller.canUsePickup());
        assertTrue(controller.canUseSuperReroll());

        assertTrue(controller.beginRerollTargeting());
        AtomicInteger rerolls = new AtomicInteger();
        actor.enterRerollTargetingMode(position -> {
            assertEquals(new GridPosition(0, 0), position);
            SymbolType replacement = controller.performReroll(position, new SymbolReroller(new Random(1)));
            actor.setInteractionEnabled(false);
            actor.animateReroll(position, replacement, () -> {
                rerolls.incrementAndGet();
                controller.setState(GridTestState.WAITING_FOR_INPUT);
                actor.setInteractionEnabled(true);
            });
        });
        tap(27.5f, 272.5f);
        advanceAnimations();
        assertEquals(1, rerolls.get());
        assertEquals(0, controller.getRemainingRerolls());
        assertEquals(3, controller.getSwapRemaining());
        assertEquals(4, controller.getMovesRemaining());
        assertTrue(moves.isEmpty());
    }

    @Test
    public void exhaustedGesturesReplaceOneHighlightWithoutChangingFinishedBoard() {
        controller.startTest(1);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        controller.applyMove(new GridMove(GridMoveType.ROW_LEFT, 0));
        controller.getBoard().setBoard(STABLE_BOARD);
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        actor.syncBoardToActors();
        for (int i = 0; i < 3; i++) {
            tap(27.5f, 272.5f);
            tap(150f, 272.5f);
            advanceAnimations();
        }
        assertEquals(3, completedSwaps);
        assertEquals(0, controller.getSwapRemaining());
        assertEquals(0, controller.getMovesRemaining());
        List<SymbolType> before = boardCells();
        controller.finish();
        actor.setInteractionEnabled(false);

        tap(-20f, 150f);
        assertHighlight(-33.75f, 88.75f, 122.5f, 122.5f);
        swipe(-20f, 150f, 60f, 150f);
        assertEquals(GridMoveType.ROW_RIGHT, moves.get(0).getType());
        assertEquals(1, moves.get(0).getIndex());
        assertHighlight(-33.75f, 88.75f, 367.5f, 122.5f);
        swipe(320f, 150f, 320f, 70f);
        assertEquals(GridMoveType.COLUMN_DOWN, moves.get(1).getType());
        assertEquals(2, moves.get(1).getIndex());
        assertHighlight(211.25f, -33.75f, 122.5f, 367.5f);

        assertEquals(before, boardCells());
        assertEquals(GridTestState.FINISHED, controller.getState());
        assertEquals(0, controller.getMovesRemaining());
        assertEquals(0, controller.getSwapRemaining());
        advanceAnimations();
        assertFalse(descendants(actor, GridActionHighlight.class).get(0).isVisible());
    }

    @Test
    public void successfulBoardIgnoresTapsAndSwipesDespiteRemainingResources() {
        for (GridSuccessTarget target : new GridSuccessTarget[] {GridSuccessTarget.ONE, GridSuccessTarget.TWO}) {
            controller.startTest(new GridTestParameters(4, 4, 2, 1, 1, 0,
                    false, false, TestMode.NORMAL, target));
            for (int i = 0; i < target.getMinimumSuccesses(); i++) {
                controller.getBoard().setBoard(
                        SymbolType.FIVE, SymbolType.FIVE, SymbolType.FIVE,
                        SymbolType.ONE, SymbolType.TWO, SymbolType.THREE,
                        SymbolType.TWO, SymbolType.THREE, SymbolType.FOUR);
                controller.resolveMatches(controller.findMatches());
            }
            actor.syncBoardToActors();
            List<SymbolType> before = boardCells();
            assertTrue(controller.shouldFinishWhenStable());
            assertEquals(GridTestResult.Outcome.SUCCESS, controller.finish().getOutcome());
            actor.setInteractionEnabled(false);
            tap(150f, 150f);
            swipe(150f, 150f, 230f, 150f);
            swipe(150f, 150f, 150f, 230f);
            assertTrue(taps.isEmpty());
            assertTrue(moves.isEmpty());
            assertEquals(before, boardCells());
            assertEquals(4, controller.getMovesRemaining());
            assertEquals(4, controller.getSwapRemaining());
        }
    }

    @Test
    public void nonAdjacentGapDoesNotBecomeSwapOrigin() {
        SymbolType[] cells = STABLE_BOARD.clone();
        cells[8] = null;
        controller.getBoard().setBoard(cells);
        actor.syncBoardToActors();
        tap(27.5f, 272.5f);
        tap(272.5f, 27.5f);
        assertEquals(1, invalidTargets);
        assertEquals(Arrays.asList(new GridPosition(0, 0)), selections);
        assertEquals(3, controller.getSwapRemaining());
        tap(150f, 272.5f);
        advanceAnimations();
        assertEquals(1, completedSwaps);
        assertEquals(2, controller.getSwapRemaining());
        assertEquals(SymbolType.TWO, controller.getBoard().getCell(0, 0));
        assertEquals(SymbolType.ONE, controller.getBoard().getCell(0, 1));
    }

    @Test
    public void retargetingOccupiedTokenStillAllowsSwapIntoAdjacentGap() {
        SymbolType[] cells = STABLE_BOARD.clone();
        cells[7] = null;
        controller.getBoard().setBoard(cells);
        actor.syncBoardToActors();
        tap(27.5f, 272.5f);
        tap(272.5f, 27.5f);
        assertEquals(Arrays.asList(new GridPosition(0, 0), new GridPosition(2, 2)), selections);
        tap(150f, 27.5f);
        advanceAnimations();
        assertEquals(1, completedSwaps);
        assertEquals(2, controller.getSwapRemaining());
        assertEquals(SymbolType.ONE, controller.getBoard().getCell(2, 1));
        assertNull(controller.getBoard().getCell(2, 2));
    }

    @Test
    public void restartDisableAndResizeDiscardPendingGestures() {
        Actor target = press(150f, 150f, 0);
        restart();
        release(target, 230f, 150f, 0);
        target = press(150f, 150f, 0);
        actor.setInteractionEnabled(false);
        actor.setInteractionEnabled(true);
        release(target, 230f, 150f, 0);
        target = press(150f, 150f, 0);
        actor.layout(150f, 150f, 300f);
        release(target, 230f, 150f, 0);
        assertTrue(moves.isEmpty());
        assertTrue(taps.isEmpty());
        swipe(150f, 150f, 230f, 150f);
        assertEquals(1, moves.size());
        assertEquals(3, controller.getMovesRemaining());
    }

    @Test
    public void secondPointerCannotReplaceAnActiveGesture() {
        Actor first = press(150f, 150f, 0);
        Actor second = press(27.5f, 272.5f, 1);
        release(second, 27.5f, 272.5f, 1);
        release(first, 230f, 150f, 0);
        assertTrue(taps.isEmpty());
        assertEquals(1, moves.size());
        assertEquals(1, moves.get(0).getIndex());
        assertEquals(GridMoveType.ROW_RIGHT, moves.get(0).getType());
    }

    @Test
    public void touchFocusCancellationDoesNotShift() {
        Actor target = press(150f, 150f, 0);
        stage.cancelTouchFocus();
        release(target, 230f, 150f, 0);
        assertTrue(moves.isEmpty());
        assertTrue(taps.isEmpty());
    }

    @Test
    public void restartDuringSwapDropsOldCompletionAndAllowsNewSwap() {
        tap(27.5f, 272.5f);
        tap(150f, 272.5f);
        actor.act(0.1f);
        restart();
        advanceAnimations();
        assertEquals(0, completedSwaps);
        assertEquals(3, controller.getSwapRemaining());
        assertEquals(Arrays.asList(STABLE_BOARD), boardCells());
        tap(27.5f, 272.5f);
        tap(150f, 272.5f);
        advanceAnimations();
        assertEquals(1, completedSwaps);
        assertEquals(2, controller.getSwapRemaining());
    }

    @Test
    public void restartDuringEveryShiftRemovesOutgoingActorAndOldCompletion() {
        for (GridMoveType type : GridMoveType.values()) {
            restart();
            AtomicInteger completed = new AtomicInteger();
            GridShiftOutcome outcome = controller.applyMove(new GridMove(type, 1));
            actor.setInteractionEnabled(false);
            actor.animateShift(outcome, completed::incrementAndGet);
            actor.act(0.1f);
            restart();
            advanceAnimations();
            assertEquals(0, completed.get());
            assertEquals(4, controller.getMovesRemaining());
            List<GridSymbolActor> symbols = descendants(actor, GridSymbolActor.class);
            assertEquals(9, symbols.size());
            for (int i = 0; i < symbols.size(); i++) {
                assertEquals(STABLE_BOARD[i], symbols.get(i).getSymbolType());
                assertEquals(1f, symbols.get(i).getColor().a, 0.001f);
            }
        }
    }

    @Test
    public void restartDuringImplosionOrRefillDropsOldCompletion() {
        for (float elapsed : new float[] {0.1f, 0.7f}) {
            AtomicInteger completed = new AtomicInteger();
            AtomicInteger refills = new AtomicInteger();
            actor.setRefillStartedListener(refills::incrementAndGet);
            List<GridPosition> cells = Arrays.asList(
                    new GridPosition(0, 0), new GridPosition(0, 1), new GridPosition(0, 2));
            List<GridMatch> matches = Arrays.asList(new GridMatch(SymbolType.ONE, cells));
            Map<GridPosition, SymbolType> replacements = new LinkedHashMap<>();
            for (GridPosition cell : cells) {
                replacements.put(cell, SymbolType.TWO);
            }
            actor.animateMatchWave(matches, replacements, completed::incrementAndGet);
            actor.act(elapsed);
            assertEquals(elapsed > 0.64f ? 1 : 0, refills.get());
            restart();
            int refillsBefore = refills.get();
            advanceAnimations();
            assertEquals(0, completed.get());
            assertEquals(refillsBefore, refills.get());
            actor.animateMatchWave(matches, replacements, completed::incrementAndGet);
            advanceAnimations();
            assertEquals(1, completed.get());
            assertEquals(refillsBefore + 1, refills.get());
        }
    }

    private void restart() {
        controller.startTest(4);
        controller.getBoard().setBoard(STABLE_BOARD);
        actor.resetAnimations();
        actor.syncBoardToActors();
        controller.setState(GridTestState.WAITING_FOR_INPUT);
        actor.setInteractionEnabled(true);
    }

    private List<SymbolType> boardCells() {
        List<SymbolType> cells = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                cells.add(controller.getBoard().getCell(row, column));
            }
        }
        return cells;
    }

    private void assertHighlight(float x, float y, float width, float height) {
        List<GridActionHighlight> highlights = descendants(actor, GridActionHighlight.class);
        assertEquals(1, highlights.size());
        GridActionHighlight highlight = highlights.get(0);
        assertTrue(highlight.isVisible());
        assertEquals(x, highlight.getX(), 0.001f);
        assertEquals(y, highlight.getY(), 0.001f);
        assertEquals(width, highlight.getWidth(), 0.001f);
        assertEquals(height, highlight.getHeight(), 0.001f);
    }

    private void tap(float x, float y) {
        swipe(x, y, x, y);
    }

    private void swipe(float x, float y, float endX, float endY) {
        Actor target = press(x, y, 0);
        release(target, endX, endY, 0);
    }

    private Actor press(float x, float y, int pointer) {
        viewport.pointerCoordinates.set(x, y);
        stage.touchDown(500, 500, pointer, 0);
        return actor.hit(x, y, true);
    }

    private void release(Actor target, float x, float y, int pointer) {
        if (target != null) {
            viewport.pointerCoordinates.set(x, y);
            stage.touchUp(500, 500, pointer, 0);
        }
    }

    private void advanceAnimations() {
        for (int i = 0; i < 40; i++) {
            actor.act(0.1f);
        }
    }

    private static <T extends Actor> List<T> descendants(Group group, Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Actor child : group.getChildren()) {
            if (type.isInstance(child)) {
                result.add(type.cast(child));
            }
            if (child instanceof Group) {
                result.addAll(descendants((Group) child, type));
            }
        }
        return result;
    }

    private static class PointerViewport extends Viewport {
        private final Vector2 pointerCoordinates = new Vector2();

        PointerViewport() {
            setWorldSize(1000f, 1000f);
        }

        @Override
        public void update(int screenWidth, int screenHeight, boolean centerCamera) {
            setScreenBounds(0, 0, screenWidth, screenHeight);
        }

        @Override
        public Vector2 unproject(Vector2 screenCoordinates) {
            return screenCoordinates.set(pointerCoordinates);
        }
    }

    private static class StubAssets implements GridBoardAssets {
        private final TextureRegion region = new TextureRegion();
        private final Array<TextureRegion> frames = new Array<>();

        StubAssets() {
            for (int i = 0; i < 16; i++) {
                frames.add(region);
            }
        }

        @Override public TextureRegion getBoardBackground() { return region; }
        @Override public TextureRegion getOverlayRegion() { return region; }
        @Override public TextureRegion getWhitePixel() { return region; }
        @Override public TextureRegion getSymbolRegion(SymbolType type) { return region; }
        @Override public Array<TextureRegion> getImplosionFrames(SymbolType type) { return frames; }
        @Override public Array<TextureRegion> getExplosionOverlayFrames() { return frames; }
        @Override public Array<TextureRegion> getImplosionOverlayFrames() { return frames; }
        @Override public Array<TextureRegion> getSpawnFrames() { return frames; }
        @Override public Array<TextureRegion> getHighlightFrames() { return frames; }
    }
}
