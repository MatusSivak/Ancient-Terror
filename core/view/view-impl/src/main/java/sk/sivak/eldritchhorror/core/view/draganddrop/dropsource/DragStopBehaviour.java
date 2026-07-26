package sk.sivak.eldritchhorror.core.view.draganddrop.dropsource;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * @author msivak
 */
public interface DragStopBehaviour<T extends Actor, U extends DragAndDrop> extends IsSourceRelated<T, U> {
    void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target);
}
