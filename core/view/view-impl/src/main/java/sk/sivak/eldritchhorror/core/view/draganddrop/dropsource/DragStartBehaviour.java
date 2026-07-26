package sk.sivak.eldritchhorror.core.view.draganddrop.dropsource;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

/**
 * @author msivak
 */
public interface DragStartBehaviour<T extends Actor, U extends DragAndDrop> extends IsSourceRelated<T, U> {
    DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer);
}
