package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.droptarget.DropBehaviour;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.components.card.CardDuplicator.duplicate;

/**
 * @author msivak
 */
public class DropBehaviourImpl implements DropBehaviour<TargetActor, CustomDragAndDrop> {

    private CustomDragAndDrop dragAndDrop;
    private TargetActor targetActor;

    private SourceDockActor sourceDockActor;
    private int indexOfActor;
    private List<TargetActorChangedListener> listeners;

    public DropBehaviourImpl(CustomDragAndDrop dragAndDrop, TargetActor targetActor) {
        this.targetActor = targetActor;
        this.dragAndDrop = dragAndDrop;
        this.listeners = new LinkedList<>();
    }

    public void addListener(TargetActorChangedListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        CardPayload cardPayload = (CardPayload) payload.getObject();
        if (cardPayload.isDockedDown()) {
            onDockedDown(source, cardPayload);
        } else {
            onDockedUp(source, cardPayload);
        }
    }

    private void onDockedUp(DragAndDrop.Source source, CardPayload cardPayload) {
        targetActor.setDocking(true);
        CardTemplate original = cardPayload.getCardTemplate();
        Vector2 destination = original.localToStageCoordinates(new Vector2(0, 0));

        for (int i = 0; i < targetActor.getChildren().size; i++) {
            if (targetActor.getChildren().get(i) == original) {
                indexOfActor = i;
            }
        }

        original.getColor().a = 1f;
        targetActor.removeActor(original);
        CardTemplate placeHolder = duplicate(original);
        placeHolder.getColor().a = 0f;
        targetActor.addActorAt(indexOfActor, placeHolder);
        original.setPosition(dragAndDrop.getDragActor().getX(), dragAndDrop.getDragActor().getY());
        dragAndDrop.getStage().addActor(original);

        MoveToAction moveToAction = Actions.moveTo(destination.x, destination.y);
        Vector2 sub = destination.sub(dragAndDrop.getDragActor().getX(), dragAndDrop.getDragActor().getY());
        moveToAction.setDuration((float) (Math.sqrt(sub.x * sub.x + sub.y * sub.y) * CustomDragAndDrop.CARD_MOVE_SPEED));
        moveToAction.setActor(original);

        source.getActor().addAction(Actions.sequence(
                moveToAction,
                Actions.run(() -> {
                    targetActor.removeActor(placeHolder);
                    targetActor.addActorAt(getIndexOfActor(), original);
                    dragAndDrop.setDraggingEnabled(true);
                })
        ));
    }


    private void onDockedDown(DragAndDrop.Source source, CardPayload cardPayload) {
        targetActor.setDocking(true);
        Vector2 destination = targetActor.getPreview().localToStageCoordinates(new Vector2(0, 0));
        CardTemplate original = cardPayload.getCardTemplate();
        original.getColor().a = 1f;

        original.remove();
        dragAndDrop.getStage().addActor(original);
        original.setPosition(dragAndDrop.getDragActor().getX(), dragAndDrop.getDragActor().getY());
        MoveToAction moveToAction = Actions.moveTo(destination.x, destination.y);
        Vector2 sub = destination.sub(dragAndDrop.getDragActor().getX(), dragAndDrop.getDragActor().getY());
        moveToAction.setDuration((float) (Math.sqrt(sub.x * sub.x + sub.y * sub.y) * CustomDragAndDrop.CARD_MOVE_SPEED));
        moveToAction.setActor(original);

        source.getActor().addAction(Actions.sequence(
                moveToAction,
                Actions.run(() -> {
                    targetActor.setPreview(null);
                    targetActor.addActor(original);
                    cardPayload.setDockedDown(false);
                    dragAndDrop.setDraggingEnabled(true);
                    for (TargetActorChangedListener listener : listeners) {
                        listener.onTargetChange(targetActor.getCardTemplates());
                    }
                })
        ));
    }

    public void setSourceDockActor(SourceDockActor sourceDockActor) {
        this.sourceDockActor = sourceDockActor;
    }

    @Override
    public CustomDragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }

    @Override
    public TargetActor getTargetActor() {
        return targetActor;
    }

    public int getIndexOfActor() {
        return indexOfActor;
    }
}
