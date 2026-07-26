package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.droptarget.ResetBehaviour;

import static sk.sivak.eldritchhorror.core.view.components.card.CardDuplicator.duplicate;

/**
 * @author msivak
 */
public class ResetBehaviourImpl implements ResetBehaviour<TargetActor, CustomDragAndDrop> {

    private CustomDragAndDrop dragAndDrop;
    private TargetActor targetActor;
    private SourceDockActor sourceDockActor;

    public ResetBehaviourImpl(CustomDragAndDrop dragAndDrop, TargetActor targetActor) {
        this.targetActor = targetActor;
        this.dragAndDrop = dragAndDrop;
    }

    @Override
    public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
        CardPayload cardPayload = (CardPayload) payload.getObject();
        if (cardPayload.isDockedDown()) {
            onDockedDown(payload);
        } else if (!targetActor.isDocking()) {
            cardPayload.getCardTemplate().getColor().a = 0f;
            if (getPreviewBottom() == null) {
                Actor dragActor = payload.getDragActor();
                setPreviewBottom(duplicate((CardTemplate) dragActor));
                getPreviewBottom().getColor().a = 0.33f;
            }
            ((CardTemplate) payload.getDragActor()).setForegroundColor(new Color(1, 0, 0, 0.25f));
            if (!sourceDockActor.getChildren().contains(getPreviewBottom(), true)) {
                sourceDockActor.addActor(getPreviewBottom());
//                sourceDockActor.invalidate();
                sourceDockActor.validate();
//                sourceDockActor.invalidateHierarchy();
            }
        }
    }

    private CardTemplate getPreviewBottom() {
        return sourceDockActor.getPreview();
    }

    private void setPreviewBottom(CardTemplate cardTemplate) {
        sourceDockActor.setPreview(cardTemplate);
    }

    public void setSourceDockActor(SourceDockActor sourceDockActor) {
        this.sourceDockActor = sourceDockActor;
    }

    private void onDockedDown(DragAndDrop.Payload payload) {
        CardPayload cardPayload = (CardPayload) payload.getObject();
        if (!targetActor.isDocking()) {
            cardPayload.getCardTemplate().getColor().a = 0.33f;
        }

        targetActor.removeActor(targetActor.getPreview());
        targetActor.setPreview(null);
        ((CardTemplate) payload.getDragActor()).setForegroundColor(new Color(1f, 1f, 1f, 0f));
    }

    @Override
    public CustomDragAndDrop getDragAndDrop() {
        return dragAndDrop;
    }

    @Override
    public TargetActor getTargetActor() {
        return targetActor;
    }
}
