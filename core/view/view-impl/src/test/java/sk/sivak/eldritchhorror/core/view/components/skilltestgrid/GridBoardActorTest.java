package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GridBoardActorTest {

    @Test
    public void symbolClipPaddingKeepsSymbolVisibleUntilItLeavesGrid() {
        assertEquals(45f, GridBoardActor.symbolClipPadding(100f), 0.001f);
    }
}
