package sk.sivak.eldritchhorror.core.view.action.trade;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.investigator.InvestigatorSketch;
import sk.sivak.eldritchhorror.core.view.draganddrop.dropsource.DragAndDropSource;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java8.features.stream.Stream.collectToList;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class TradeDragAndDropBinder extends DragAndDropBinder {

    private InvestigatorId sourceInvestigatorId;
    private InvestigatorId targetInvestigatorId;

    public TradeDragAndDropBinder(Stage stage) {
        super(stage, new TargetActorChangedListenerImpl());
    }

    public void init(CardTemplate[] sourceCardTemplates, CardTemplate[] targetCardTemplates,
                     InvestigatorId sourceInvestigatorId, InvestigatorId targetInvestigatorId) {
        this.sourceInvestigatorId = sourceInvestigatorId;
        this.targetInvestigatorId = targetInvestigatorId;
        ((TargetActorChangedListenerImpl) listener).setSourceCardTemplates(sourceCardTemplates);
        ((TargetActorChangedListenerImpl) listener).setTargetCardTemplates(targetCardTemplates);
        List<CardClickListener> allListeners = new LinkedList<>();
        for (CardTemplate currentTemplate : sourceCardTemplates) {
            initCardTemplate(sourceCardTemplates, allListeners, currentTemplate);
        }

        for (CardTemplate currentTemplate : targetCardTemplates) {
            initCardTemplate(targetCardTemplates, allListeners, currentTemplate);
        }

        for (CardClickListener currentListener : allListeners) {
            currentListener.setOtherListeners(collectToList(allListeners, listener -> !listener.equals(currentListener)));
        }

        initSourceDockActor(sourceCardTemplates, targetCardTemplates);
        initTargetActor(sourceCardTemplates, targetCardTemplates);

        sourceTargetGroup.setWidth(getSourceDockActor().getWidth());
        sourceTargetGroup.setHeight(getSourceDockActor().getHeight() + getTargetActor().getHeight() + VIEWPORT_HEIGHT * 0.1f);
        sourceTargetGroup.setPosition(VIEWPORT_WIDTH / 2 - sourceTargetGroup.getWidth() / 2,
                VIEWPORT_HEIGHT / 2 - sourceTargetGroup.getHeight() / 2);
    }

    private void initSourceDockActor(CardTemplate[] sourceCardTemplates, CardTemplate[] targetCardTemplates) {
        getSourceDockActor().init(sourceCardTemplates);
        float prefWidth;
        float prefHeight;
        if (sourceCardTemplates.length == 0 && targetCardTemplates.length == 0) {
            return;
        } else if (sourceCardTemplates.length > 0) {
            prefWidth = sourceCardTemplates[0].getPrefWidth();
            prefHeight = sourceCardTemplates[0].getPrefHeight();
        } else {
            prefWidth = targetCardTemplates[0].getPrefWidth();
            prefHeight = targetCardTemplates[0].getPrefHeight();
        }
        getSourceDockActor().setSize(prefWidth * (sourceCardTemplates.length + targetCardTemplates.length) + 180, prefHeight);

        Container<InvestigatorSketch> investigaatorSketchContainer = new Container<>(new InvestigatorSketch(sourceInvestigatorId));
        investigaatorSketchContainer.getActor().select();
        investigaatorSketchContainer.padRight(5);
        investigaatorSketchContainer.width(175);
        investigaatorSketchContainer.height(prefHeight);
        getSourceDockActor().addActorAt(0, investigaatorSketchContainer);
    }

    private void initTargetActor(CardTemplate[] sourceCardTemplates, CardTemplate[] targetCardTemplates) {
        getTargetActor().init(sourceCardTemplates);

        for (CardTemplate cardTemplate : targetCardTemplates) {
            getTargetActor().addActor(cardTemplate);
        }

        getTargetActor().setSize(getSourceDockActor().getWidth(), getSourceDockActor().getHeight());

        Container<InvestigatorSketch> investigaatorSketchContainer = new Container<>(new InvestigatorSketch(targetInvestigatorId));
        investigaatorSketchContainer.padRight(5);
        investigaatorSketchContainer.getActor().select();
        investigaatorSketchContainer.width(175);
        investigaatorSketchContainer.height(getSourceDockActor().getHeight());
        getTargetActor().addActorAt(0, investigaatorSketchContainer);

    }

    private void initCardTemplate(CardTemplate[] sourceCardTemplates, List<CardClickListener> allListeners, CardTemplate currentTemplate) {
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
        setAllTemplates(listener, sourceCardTemplates);
        currentTemplate.addListener(listener);
        allListeners.add(listener);
    }

    public List<CardInfo> getSourceCards() {
        return ((TargetActorChangedListenerImpl) listener).getSourceCards();
    }

    public List<CardInfo> getTargetCards() {
        return ((TargetActorChangedListenerImpl) listener).getTargetCards();
    }

    private static class TargetActorChangedListenerImpl implements TargetActorChangedListener {

        private List<CardTemplate> allCards = new LinkedList<>();
        private List<CardTemplate> cardsOnTarget = new LinkedList<>();
        private List<CardTemplate> cardsOnSource = new LinkedList<>();

        @Override
        public void onTargetChange(List<CardTemplate> newCardsOnTarget) {

            cardsOnTarget.clear();
            cardsOnSource.clear();
            for (CardTemplate card : allCards) {
                if (newCardsOnTarget.contains(card)) {
                    cardsOnTarget.add(card);
                } else {
                    cardsOnSource.add(card);
                }
            }
        }

        public void setSourceCardTemplates(CardTemplate[] sourceCardTemplates) {
            allCards.addAll(Arrays.asList(sourceCardTemplates));
        }

        public void setTargetCardTemplates(CardTemplate[] targetCardTemplates) {
            allCards.addAll(Arrays.asList(targetCardTemplates));
        }

        public List<CardInfo> getSourceCards() {
            return new ArrayList<>(Stream.map(cardsOnSource, CardTemplate::getCardInfo));
        }

        public List<CardInfo> getTargetCards() {
            return new ArrayList<>(Stream.map(cardsOnTarget, CardTemplate::getCardInfo));
        }
    }
}
