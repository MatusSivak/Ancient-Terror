package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.dropsource.DragAndDropSource;
import sk.sivak.eldritchhorror.core.view.draganddrop.droptarget.DragAndDropTarget;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java8.features.stream.Stream.collectToList;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

/**
 * @author msivak
 */
public class DragAndDropBinder {

    protected TargetActorChangedListener listener;
    protected CustomDragAndDrop customDragAndDrop;
    protected SourceTargetGroup sourceTargetGroup;

    public DragAndDropBinder(Stage stage, TargetActorChangedListener listener) {
        customDragAndDrop = createCustomDragAndDrop();
        this.listener = listener;
        customDragAndDrop.setStage(stage);

        sourceTargetGroup = createSourceTargetGroup();


        DragAndDropTarget<TargetActor> target = new DragAndDropTarget<>(getTargetActor());
        DragBehaviourImpl dragBehaviour = new DragBehaviourImpl(customDragAndDrop, getTargetActor());
        dragBehaviour.setSourceDockActor(getSourceDockActor());
        target.setDragBehaviour(dragBehaviour);
        DropBehaviourImpl dropBehaviour = new DropBehaviourImpl(customDragAndDrop, getTargetActor());
        dropBehaviour.setSourceDockActor(getSourceDockActor());
        dropBehaviour.addListener(listener);
        target.setDropBehaviour(dropBehaviour);
        ResetBehaviourImpl resetBehaviour = new ResetBehaviourImpl(customDragAndDrop, getTargetActor());
        resetBehaviour.setSourceDockActor(getSourceDockActor());
        target.setResetBehaviour(resetBehaviour);
        customDragAndDrop.addTarget(target);
    }

    protected CustomDragAndDrop createCustomDragAndDrop() {
        return new CustomDragAndDrop();
    }

    protected SourceTargetGroup createSourceTargetGroup() {
        return new SourceTargetGroup();
    }

    public void init(CardTemplate... cardTemplates) {

        if (cardTemplates == null || cardTemplates.length == 0) {
            sourceTargetGroup.remove();
            return;
        }

        List<CardClickListener> allListeners = new LinkedList<>();
        for (CardTemplate currentTemplate : cardTemplates) {
            DragAndDropSource<CardTemplate> source = new DragAndDropSource<>(currentTemplate);
            DragStartBehaviourImpl dragStartBehaviour = createDragStartBehaviour(currentTemplate);
            source.setDragStartBehaviour(dragStartBehaviour);
            DragStopBehaviourImpl dragStopBehaviour = new DragStopBehaviourImpl(customDragAndDrop, currentTemplate);
            dragStopBehaviour.addListener(listener);
            dragStopBehaviour.setTargetActor(sourceTargetGroup.getTargetActor());
            dragStopBehaviour.setSourceDockActor(getSourceDockActor());
            source.setDragStopBehaviour(dragStopBehaviour);
            customDragAndDrop.addSource(source);

            CardClickListener listener = new CardClickListener();
            setAllTemplates(listener, cardTemplates);
            currentTemplate.addListener(listener);
            allListeners.add(listener);
        }

        for (CardClickListener currentListener : allListeners) {
            currentListener.setOtherListeners(collectToList(allListeners, listener -> !listener.equals(currentListener)));
        }

        getSourceDockActor().init(cardTemplates);
        getTargetActor().init(cardTemplates);
        sourceTargetGroup.setWidth(getSourceDockActor().getWidth());
        sourceTargetGroup.setHeight(getSourceDockActor().getHeight() + getTargetActor().getHeight() + VIEWPORT_HEIGHT * 0.1f);
        sourceTargetGroup.setPosition(VIEWPORT_WIDTH / 2 - sourceTargetGroup.getWidth() / 2,
                VIEWPORT_HEIGHT / 2 - sourceTargetGroup.getHeight() / 2);
    }

    protected void setAllTemplates(CardClickListener listener, CardTemplate[] cardTemplates) {
        listener.setAllTemplates(Arrays.asList(cardTemplates));
    }

    protected DragStartBehaviourImpl createDragStartBehaviour(CardTemplate cardTemplate) {
        DragStartBehaviourImpl dragStartBehaviour = new DragStartBehaviourImpl(customDragAndDrop, cardTemplate);
        dragStartBehaviour.setTargetActor(getTargetActor());
        return dragStartBehaviour;
    }

    protected TargetActor getTargetActor() {
        return getSourceTargetGroup().getTargetActor();
    }

    protected SourceDockActor getSourceDockActor() {
        return getSourceTargetGroup().getSourceDockActor();
    }

    public SourceTargetGroup getSourceTargetGroup() {
        return sourceTargetGroup;
    }
}
