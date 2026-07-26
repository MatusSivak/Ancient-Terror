package sk.sivak.eldritchhorror.core.view.draganddrop.impl.reserve;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import java8.features.function.Consumer;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.CustomDragAndDrop;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.TargetActorChangedListener;

import java.util.LinkedList;
import java.util.List;

import static java8.features.stream.Stream.collectToList;
import static java8.features.stream.Stream.map;

public class ReserveTargetActorChangedListener implements TargetActorChangedListener {

    private ReserveSourceTargetGroup sourceTargetGroup;
    private int valueLimit;
    private int selectedAssetsValue;
    private CustomDragAndDrop dragAndDrop;
    private List<CardTemplate> cards = new LinkedList<>();

    public ReserveTargetActorChangedListener() {
    }

    @Override
    public void onTargetChange(List<CardTemplate> cards) {
        this.cards = cards;
        selectedAssetsValue = 0;
        for (CardTemplate card : cards) {
            selectedAssetsValue += card.getAssetInfo().getCost();
        }

        if (selectedAssetsValue > valueLimit) {
            for (CardTemplate card : cards) {
                sourceTargetGroup.getSourceDockActor().addActor(card);
            }
            selectedAssetsValue = 0;
            this.cards = new LinkedList<>();
        }
        sourceTargetGroup.updateRemainingValue(valueLimit - selectedAssetsValue);
        disableEnableDragging();
        fireTargetChanged(this.cards);
    }

    private void fireTargetChanged(List<CardTemplate> cards) {
        for (Consumer<List<CardTemplate>> childListener : childListeners) {
            childListener.accept(cards);
        }
    }

    public void setDragAndDrop(CustomDragAndDrop dragAndDrop) {
        this.dragAndDrop = dragAndDrop;
    }

    private void disableAllSourceActors() {
        for (Actor actor : sourceTargetGroup.getSourceDockActor().getChildren()) {
            disableCardTemplate(((CardTemplate) actor));
        }
    }

    private void enableAllSourceActors() {
        for (Actor actor : sourceTargetGroup.getSourceDockActor().getChildren()) {
            enableCardTemplate(((CardTemplate) actor));
        }
    }

    private void enableCardTemplate(CardTemplate cardTemplate) {
        for (DragAndDrop.Source source : dragAndDrop.getAllSources()) {
            if (source.getActor().equals(cardTemplate)) {
                dragAndDrop.enableSource(source);
                cardTemplate.setForegroundColor(new Color(0f, 0f, 0f, 0));
            }
        }
    }

    private void disableCardTemplate(CardTemplate cardTemplate) {
        for (DragAndDrop.Source source : dragAndDrop.getAllSources()) {
            if (source.getActor().equals(cardTemplate)) {
                dragAndDrop.disableSource(source);
                cardTemplate.setForegroundColor(new Color(0f, 0f, 0f, 0.66f));
            }
        }
    }

    List<AssetInfo> getSelectedAssets() {
        return collectToList(map(cards, CardTemplate::getAssetInfo));
    }

    void setSourceTargetGroup(ReserveSourceTargetGroup sourceTargetGroup) {
        this.sourceTargetGroup = sourceTargetGroup;
    }

    void setValueLimit(int valueLimit) {
        this.valueLimit = valueLimit;
        sourceTargetGroup.updateRemainingValue(valueLimit - selectedAssetsValue);
        fireTargetChanged(cards);
    }

    private void disableEnableDragging() {
        enableAllSourceActors();

        for (Actor actor : sourceTargetGroup.getSourceDockActor().getChildren()) {
            CardTemplate cardTemplate = (CardTemplate) actor;
            if (cardTemplate.getAssetInfo().getCost() + selectedAssetsValue > valueLimit) {
                disableCardTemplate(cardTemplate);
            }
        }
    }

    public void init() {
        disableEnableDragging();
    }

    private List<Consumer<List<CardTemplate>>> childListeners = new LinkedList<>();

    public void addChildListener(Consumer<List<CardTemplate>> childListener) {
        childListeners.add(childListener);
    }
}
