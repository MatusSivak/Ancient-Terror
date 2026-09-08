package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridBoardActorTest {

    @Test
    public void symbolClipPaddingKeepsSymbolVisibleUntilItLeavesGrid() {
        assertEquals(45f, GridBoardActor.symbolClipPadding(100f), 0.001f);
    }

    @Test
    public void tapsAllowSmallPointerMovement() {
        assertTrue(GridBoardActor.isTap(0f, 0f, 100f));
        assertTrue(GridBoardActor.isTap(10f, -10f, 100f));
    }

    @Test
    public void swipesInEveryDirectionAreNotTaps() {
        assertFalse(GridBoardActor.isTap(40f, 0f, 100f));
        assertFalse(GridBoardActor.isTap(-40f, 0f, 100f));
        assertFalse(GridBoardActor.isTap(0f, 40f, 100f));
        assertFalse(GridBoardActor.isTap(0f, -40f, 100f));
    }

    @Test
    public void tapThresholdScalesWithCellSize() {
        assertTrue(GridBoardActor.isTap(20f, 0f, 100f));
        assertFalse(GridBoardActor.isTap(20f, 0f, 50f));
        assertFalse(GridBoardActor.isTap(0f, 0f, 0f));
    }
}
