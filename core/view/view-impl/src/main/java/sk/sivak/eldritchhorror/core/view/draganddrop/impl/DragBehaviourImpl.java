package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.droptarget.DragBehaviour;

import static sk.sivak.eldritchhorror.core.view.components.card.CardDuplicator.duplicate;

/**
 * @author msivak
 */
public class DragBehaviourImpl implements DragBehaviour<TargetActor, CustomDragAndDrop> {

    private CustomDragAndDrop dragAndDrop;
    private TargetActor targetActor;
    private SourceDockActor sourceDockActor;

    public DragBehaviourImpl(CustomDragAndDrop dragAndDrop, TargetActor targetActor) {
        this.targetActor = targetActor;
        this.dragAndDrop = dragAndDrop;
    }

    @Override
    public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
        targetActor.setDocking(false);
        CardPayload cardPayload = (CardPayload) payload.getObject();
        if (cardPayload.isDockedDown()) {
            return onDockedDown(payload);
        } else {
            cardPayload.getCardTemplate().getColor().a = 0.33f;
            if (sourceDockActor.getPreview() != null) {
                sourceDockActor.getPreview().remove();
            }
            ((CardTemplate) payload.getDragActor()).setForegroundColor(new Color(1f, 1f, 1f, 0f));
        }
        return true;
    }


    private boolean onDockedDown(DragAndDrop.Payload payload) {
        if (getPreviewTop() == null) {
            Actor dragActor = payload.getDragActor();
            setPreviewTop(duplicate((CardTemplate) dragActor));
            getPreviewTop().getColor().a = 0.33f;
        }
        ((CardTemplate) payload.getDragActor()).setForegroundColor(new Color(0, 1, 0, 0.25f));
        ((CardPayload) payload.getObject()).getCardTemplate().getColor().a = 0f;
        if (!targetActor.getChildren().contains(getPreviewTop(), true)) {
            targetActor.addActor(getPreviewTop());
        }
        return true;
    }

    private CardTemplate getPreviewTop() {
        return targetActor.getPreview();
    }

    private void setPreviewTop(CardTemplate preview) {
        targetActor.setPreview(preview);
    }

    public void setSourceDockActor(SourceDockActor sourceDockActor) {
        this.sourceDockActor = sourceDockActor;
    }

    @Override
    public TargetActor getTargetActor() {
        return targetActor;
    }

    @Override
    public CustomDragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }
}
