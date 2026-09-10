package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridActionHighlightTest {
    @Test
    public void rowAttemptsHighlightOnlyTheSelectedRowInBothDirections() {
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable());
        for (GridMoveType type : new GridMoveType[] {GridMoveType.ROW_LEFT, GridMoveType.ROW_RIGHT}) {
            for (int row = 0; row < GridBoard.SIZE; row++) {
                highlight.show(new GridMove(type, row), 100f, 0f, 0f);

                assertBounds(highlight, 0f, (2 - row) * 100f, 300f, 100f);
            }
        }
    }

    @Test
    public void columnAttemptsHighlightOnlyTheSelectedColumnInBothDirections() {
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable());
        for (GridMoveType type : new GridMoveType[] {GridMoveType.COLUMN_UP, GridMoveType.COLUMN_DOWN}) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                highlight.show(new GridMove(type, column), 80f, 0f, 0f);

                assertBounds(highlight, column * 80f, 0f, 80f, 240f);
            }
        }
    }

    @Test
    public void newAttemptReplacesPreviousLineAndRestartsRedFlash() {
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable());
        highlight.show(new GridMove(GridMoveType.ROW_LEFT, 0), 100f, 0f, 0f);
        highlight.act(0.2f);
        highlight.act(0.2f);

        highlight.show(new GridMove(GridMoveType.COLUMN_DOWN, 1), 100f, 0f, 0f);

        assertBounds(highlight, 100f, 0f, 100f, 300f);
        assertEquals(1, highlight.getActions().size);
        assertEquals(1f, highlight.getColor().r, 0f);
        assertEquals(0.02f, highlight.getColor().g, 0f);
        assertEquals(0.04f, highlight.getColor().b, 0f);
        assertEquals(1f, highlight.getColor().a, 0f);
        assertEquals(Touchable.disabled, highlight.getTouchable());
    }

    @Test
    public void flashDisappearsAfterAnimation() {
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable());
        highlight.show(new GridMove(GridMoveType.ROW_RIGHT, 2), 100f, 0f, 0f);

        for (int i = 0; i < 10; i++) {
            highlight.act(0.1f);
        }

        assertFalse(highlight.isVisible());
        assertEquals(0, highlight.getActions().size);
    }

    @Test
    public void hidingClearsFeedbackAndPendingAnimation() {
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable());
        assertFalse(highlight.isVisible());
        highlight.show(new GridMove(GridMoveType.COLUMN_UP, 2), 100f, 0f, 0f);

        highlight.hide();

        assertFalse(highlight.isVisible());
        assertEquals(0, highlight.getActions().size);
    }

    @Test
    public void expandedVisualCellsAlignHighlightsWithTokenSpacing() {
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable());
        float visualCellSize = 120f;
        float origin = -30f;

        highlight.show(new GridMove(GridMoveType.ROW_LEFT, 1), visualCellSize, origin, origin);
        assertBounds(highlight, -30f, 90f, 360f, 120f);

        highlight.show(new GridMove(GridMoveType.COLUMN_DOWN, 2), visualCellSize, origin, origin);
        assertBounds(highlight, 210f, -30f, 120f, 360f);
    }

    @Test
    public void softGlowStaysInsideOneLineAndRestoresBatchColor() {
        final int[] draws = {0};
        final float[] peakOpacity = {0f};
        final boolean[] restoredColor = {false};
        final float previousColor = Color.BLUE.toFloatBits();
        Batch batch = (Batch) Proxy.newProxyInstance(Batch.class.getClassLoader(), new Class<?>[] {Batch.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("getPackedColor")) {
                        return previousColor;
                    }
                    if (method.getName().equals("setColor")) {
                        if (arguments.length == 1) {
                            assertEquals(previousColor, (Float) arguments[0], 0f);
                            restoredColor[0] = true;
                        } else {
                            assertEquals(1f, (Float) arguments[0], 0f);
                            assertEquals(0.02f, (Float) arguments[1], 0f);
                            assertEquals(0.04f, (Float) arguments[2], 0f);
                            assertTrue((Float) arguments[3] > 0f);
                            assertTrue((Float) arguments[3] <= 0.70f);
                            if (draws[0] == 0) {
                                assertEquals(0.25f, (Float) arguments[3], 0f);
                            }
                            peakOpacity[0] = Math.max(peakOpacity[0], (Float) arguments[3]);
                        }
                        return null;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable() {
            @Override
            public void draw(Batch batch, float x, float y, float width, float height) {
                assertTrue(x >= 10f && y >= 120f);
                assertTrue(width > 0f && height > 0f);
                assertTrue(x + width <= 310.001f);
                assertTrue(y + height <= 220.001f);
                draws[0]++;
            }
        });
        highlight.show(new GridMove(GridMoveType.ROW_LEFT, 1), 100f, 10f, 20f);

        highlight.draw(batch, 1f);

        assertTrue(draws[0] > 5);
        assertTrue(peakOpacity[0] > 0.60f);
        assertTrue(restoredColor[0]);
    }

    @Test
    public void swapAttemptsHighlightOnlyTheTappedCell() {
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable());
        for (int row = 0; row < GridBoard.SIZE; row++) {
            for (int column = 0; column < GridBoard.SIZE; column++) {
                highlight.show(new GridPosition(row, column), 120f, -30f, -30f);

                assertBounds(highlight, -30f + column * 120f, -30f + (2 - row) * 120f, 120f, 120f);
                assertEquals(1f, highlight.getColor().r, 0f);
                assertEquals(0.02f, highlight.getColor().g, 0f);
                assertEquals(0.04f, highlight.getColor().b, 0f);
            }
        }
    }

    @Test
    public void shiftAndSwapFeedbackReplaceEachOtherInsteadOfAccumulating() {
        GridActionHighlight highlight = new GridActionHighlight(new BaseDrawable());
        highlight.show(new GridMove(GridMoveType.ROW_LEFT, 0), 100f, 0f, 0f);
        highlight.show(new GridPosition(1, 1), 100f, 0f, 0f);

        assertBounds(highlight, 100f, 100f, 100f, 100f);
        assertEquals(1, highlight.getActions().size);

        highlight.show(new GridMove(GridMoveType.COLUMN_UP, 0), 100f, 0f, 0f);

        assertBounds(highlight, 0f, 0f, 100f, 300f);
        assertEquals(1, highlight.getActions().size);
    }

    private void assertBounds(GridActionHighlight highlight, float x, float y, float width, float height) {
        assertTrue(highlight.isVisible());
        assertEquals(x, highlight.getX(), 0.001f);
        assertEquals(y, highlight.getY(), 0.001f);
        assertEquals(width, highlight.getWidth(), 0.001f);
        assertEquals(height, highlight.getHeight(), 0.001f);
    }
}
