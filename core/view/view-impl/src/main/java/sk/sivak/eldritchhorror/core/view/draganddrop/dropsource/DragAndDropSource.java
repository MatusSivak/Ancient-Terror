package sk.sivak.eldritchhorror.core.view.draganddrop.dropsource;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * @author msivak
 */
public class DragAndDropSource<T extends Actor> extends DragAndDrop.Source {

    private DragStopBehaviour dragStopBehaviour;
    private DragStartBehaviour dragStartBehaviour;

    public DragAndDropSource(T actor) {
        super(actor);
    }

    public DragStopBehaviour getDragStopBehaviour() {
        return dragStopBehaviour;
    }

    public void setDragStopBehaviour(DragStopBehaviour dragStopBehaviour) {
        this.dragStopBehaviour = dragStopBehaviour;
    }

    public DragStartBehaviour getDragStartBehaviour() {
        return dragStartBehaviour;
    }

    public void setDragStartBehaviour(DragStartBehaviour dragStartBehaviour) {
        this.dragStartBehaviour = dragStartBehaviour;
    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        if (dragStartBehaviour != null) {
            return dragStartBehaviour.dragStart(event, x, y, pointer);
        }
        return null;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        if (dragStopBehaviour != null) {
            dragStopBehaviour.dragStop(event, x, y, pointer, payload, target);
        }
    }

}
