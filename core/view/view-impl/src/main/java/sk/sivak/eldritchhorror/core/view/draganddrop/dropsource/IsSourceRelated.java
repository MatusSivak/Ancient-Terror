package sk.sivak.eldritchhorror.core.view.draganddrop.dropsource;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * @author msivak
 */
public interface IsSourceRelated<T extends Actor, U extends DragAndDrop> {
    T getSourceActor();

    U getDragAndDrop();
}
