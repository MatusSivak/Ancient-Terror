package sk.sivak.eldritchhorror.core.view.draganddrop.droptarget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * @author msivak
 */
public interface DragBehaviour<T extends Actor, U extends DragAndDrop> extends IsTargetRelated<T, U> {
    boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer);
}
