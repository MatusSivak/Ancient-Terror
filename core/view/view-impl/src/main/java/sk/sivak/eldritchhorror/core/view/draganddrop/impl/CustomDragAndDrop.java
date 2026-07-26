package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class CustomDragAndDrop extends DragAndDrop {

    public final static float CARD_MOVE_SPEED = 0.0007f;
    private float dragActorX;
    private float dragActorY;
    private Stage stage;
    private List<Source> allSources = new LinkedList<>();
    private List<Source> enabledSources = new LinkedList<>();

    public CustomDragAndDrop() {
        setDragTime(0);
        setTapSquareSize(1);
    }

    @Override
    public void setDragActorPosition(float dragActorX, float dragActorY) {
        super.setDragActorPosition(dragActorX, dragActorY);
        this.dragActorX = dragActorX;
        this.dragActorY = dragActorY;
    }

    public void setDraggingEnabled(boolean draggingEnabled) {
        for (Source source : allSources) {
            source.getActor().setTouchable(draggingEnabled ? Touchable.enabled : Touchable.disabled);
        }
    }

    public List<Source> getAllSources() {
        return allSources;
    }

    private List<Source> getEnabledSources() {
        return enabledSources;
    }

    @Override
    public void addSource(Source source) {
        super.addSource(source);
        getAllSources().add(source);
        getEnabledSources().add(source);
    }

    public void enableSource(Source source) {
        if (!getEnabledSources().contains(source)) {
            getEnabledSources().add(source);
            super.addSource(source);
        }
    }

    public void disableSource(Source source) {
        if (getEnabledSources().contains(source)) {
            getEnabledSources().remove(source);
            super.removeSource(source);
        }
    }

    public float getDragActorX() {
        return dragActorX;
    }

    public float getDragActorY() {
        return dragActorY;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
