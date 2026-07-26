package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.dropsource.DragStopBehaviour;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.view.components.card.CardDuplicator.duplicate;
import static sk.sivak.eldritchhorror.core.view.draganddrop.impl.CustomDragAndDrop.CARD_MOVE_SPEED;

/**
 * @author msivak
 */
public class DragStopBehaviourImpl implements DragStopBehaviour<CardTemplate, CustomDragAndDrop> {

    private CardTemplate sourceActor;
    private CustomDragAndDrop dragAndDrop;
    private SourceDockActor sourceDockActor;
    private List<TargetActorChangedListener> listeners;
    private TargetActor targetActor;

    public DragStopBehaviourImpl(CustomDragAndDrop dragAndDrop, CardTemplate sourceActor) {
        this.sourceActor = sourceActor;
        this.dragAndDrop = dragAndDrop;
        listeners = new LinkedList<>();
    }

    public void addListener(TargetActorChangedListener listener) {
        this.listeners.add(listener);
    }



    public void setTargetActor(TargetActor targetActor) {
        this.targetActor = targetActor;
    }

    @Override
    public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
        if (target == null) {
            CardPayload cardPayload = (CardPayload) payload.getObject();
            if (cardPayload.isDockedDown()) {
                onDockedDown(payload, cardPayload);
            } else {
                onDockedUp(payload, cardPayload);
            }
        }
    }

    public void setSourceDockActor(SourceDockActor sourceDockActor) {
        this.sourceDockActor = sourceDockActor;
    }

    private void onDockedUp(DragAndDrop.Payload payload, CardPayload cardPayload) {
        CardTemplate original = cardPayload.getCardTemplate();
        original.getColor().a = 0f;
        original.setTouchable(Touchable.disabled);

        CardTemplate preview = sourceDockActor.getPreview();
        if (preview == null) {
            return; // to prevent exception
        }
        Vector2 destinationPosition = preview.localToStageCoordinates(new Vector2(0, 0));
        System.out.println("Preview position: " + destinationPosition);
        for (Actor actor : sourceDockActor.getChildren()) {
            System.out.println("Actor: " + actor + " - " + actor.localToStageCoordinates(new Vector2(0, 0)));
        }
        MoveToAction moveToAction = Actions.moveTo(destinationPosition.x, destinationPosition.y);


        CardTemplate cardTemplate = duplicate(original);

        Vector2 sub = new Vector2(destinationPosition.x, destinationPosition.y)
                .sub(payload.getDragActor().getX(), payload.getDragActor().getY());
        moveToAction.setDuration((float) (Math.sqrt(sub.x * sub.x + sub.y * sub.y) * CARD_MOVE_SPEED));
        moveToAction.setActor(cardTemplate);

        SequenceAction moveAndRemove = Actions.sequence(moveToAction, Actions.run(() -> {
            original.remove();
            cardTemplate.remove();
            preview.remove();
            sourceDockActor.setPreview(null);
            sourceDockActor.addActor(original);
            original.getColor().a = 1;
            original.setTouchable(Touchable.enabled);
            dragAndDrop.setDraggingEnabled(true);
            for (TargetActorChangedListener listener : listeners) {
                listener.onTargetChange(targetActor.getCardTemplates());
            }
        }));

        cardTemplate.setPosition(payload.getDragActor().getX(), payload.getDragActor().getY());
        cardTemplate.addAction(moveAndRemove);

        dragAndDrop.getStage().addActor(cardTemplate);
    }

    private void onDockedDown(DragAndDrop.Payload payload, CardPayload cardPayload) {
        CardTemplate original = cardPayload.getCardTemplate();
        original.getColor().a = 0f;
        original.setTouchable(Touchable.disabled);

        Vector2 destinationPosition = original.localToStageCoordinates(new Vector2(0, 0));

        MoveToAction moveToAction = Actions.moveTo(destinationPosition.x, destinationPosition.y);

        CardTemplate cardTemplate = duplicate(original);

        Vector2 sub = new Vector2(destinationPosition.x, destinationPosition.y)
                .sub(payload.getDragActor().getX(), payload.getDragActor().getY());
        moveToAction.setDuration((float) (Math.sqrt(sub.x * sub.x + sub.y * sub.y) * CARD_MOVE_SPEED));
        moveToAction.setActor(cardTemplate);

        SequenceAction moveAndRemove = Actions.sequence(moveToAction, Actions.run(() -> {
            cardTemplate.remove();
            original.getColor().a = 1;
            original.setTouchable(Touchable.enabled);
            dragAndDrop.setDraggingEnabled(true);
        }));

        cardTemplate.setPosition(payload.getDragActor().getX(), payload.getDragActor().getY());
        cardTemplate.addAction(moveAndRemove);

        dragAndDrop.getStage().addActor(cardTemplate);
    }

    @Override
    public CardTemplate getSourceActor() {
        return sourceActor;
    }

    @Override
    public CustomDragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }
}
