package sk.sivak.eldritchhorror.core.view.draganddrop;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * @author msivak
 */
public class DragAndDropWrapper {

    private DragAndDrop dragAndDrop;

    public DragAndDropWrapper() {
        dragAndDrop = new DragAndDrop();
        dragAndDrop.setTapSquareSize(0f);
    }
}
