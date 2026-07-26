package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;

import java.util.List;

/**
 * @author msivak
 */
public interface TargetActorChangedListener {
    void onTargetChange(List<CardTemplate> cards);
}
