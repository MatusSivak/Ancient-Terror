package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.dropsource.DragStartBehaviour;

/**
 * @author msivak
 */
public class DragStartBehaviourImpl implements DragStartBehaviour<CardTemplate, CustomDragAndDrop> {

    protected CustomDragAndDrop dragAndDrop;
    private CardTemplate sourceActor;
    private TargetActor targetActor;

    public DragStartBehaviourImpl(CustomDragAndDrop dragAndDrop, CardTemplate sourceActor) {
        this.sourceActor = sourceActor;
        this.dragAndDrop = dragAndDrop;
    }

    public void setTargetActor(TargetActor targetActor) {
        this.targetActor = targetActor;
    }

    @Override
    public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
        dragAndDrop.setDraggingEnabled(false);
        targetActor.setPreview(null);
        DragAndDrop.Payload payload = new DragAndDrop.Payload();
        CardTemplate original = (CardTemplate) event.getTarget().getParent();
        original.getColor().a = 0.33f;
        CardTemplate dragActor = CardTemplate.buildCard(original.getCardInfo());
        dragActor.setScaleX(original.getScaleX());
        dragActor.setScaleY(original.getScaleY());
        float dragActorPosition = dragActor.getWidth() - x * dragActor.getScaleX();
        dragAndDrop.setDragActorPosition(dragActorPosition,
                -y * dragActor.getScaleY());
        payload.setDragActor(dragActor);
        CardPayload cardPayload = new CardPayload(original);
        cardPayload.setDockedDown(original.getParent() != targetActor);
        payload.setObject(cardPayload);
        return payload;
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
