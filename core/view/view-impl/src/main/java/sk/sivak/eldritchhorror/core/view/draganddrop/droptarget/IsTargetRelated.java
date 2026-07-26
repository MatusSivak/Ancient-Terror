package sk.sivak.eldritchhorror.core.view.draganddrop.droptarget;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * @author msivak
 */
public interface IsTargetRelated<T extends Actor, U extends DragAndDrop> {
    T getTargetActor();

    U getDragAndDrop();
}
