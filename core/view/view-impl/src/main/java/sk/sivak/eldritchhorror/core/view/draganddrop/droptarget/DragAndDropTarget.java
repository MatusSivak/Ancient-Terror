package sk.sivak.eldritchhorror.core.view.draganddrop.droptarget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * @author msivak
 */
public class DragAndDropTarget<T extends Actor> extends DragAndDrop.Target {

    private DragBehaviour dragBehaviour;
    private DropBehaviour dropBehaviour;
    private ResetBehaviour resetBehaviour;

    public DragAndDropTarget(T actor) {
        super(actor);
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (dragBehaviour != null) {
            return dragBehaviour.drag(source, payload, x, y, pointer);
        }
        return false;
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        if (dropBehaviour != null) {
            dropBehaviour.drop(source, payload, x, y, pointer);
        }
    }

    @Override
    public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
        if (resetBehaviour != null) {
            resetBehaviour.reset(source, payload);
        }
    }

    public DragBehaviour getDragBehaviour() {
        return dragBehaviour;
    }

    public void setDragBehaviour(DragBehaviour dragBehaviour) {
        this.dragBehaviour = dragBehaviour;
    }

    public DropBehaviour getDropBehaviour() {
        return dropBehaviour;
    }

    public void setDropBehaviour(DropBehaviour dropBehaviour) {
        this.dropBehaviour = dropBehaviour;
    }

    public ResetBehaviour getResetBehaviour() {
        return resetBehaviour;
    }

    public void setResetBehaviour(ResetBehaviour resetBehaviour) {
        this.resetBehaviour = resetBehaviour;
    }
}
