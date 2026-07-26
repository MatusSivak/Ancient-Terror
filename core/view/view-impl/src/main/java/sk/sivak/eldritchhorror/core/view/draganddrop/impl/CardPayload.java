package sk.sivak.eldritchhorror.core.view.draganddrop.impl;

import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;

/**
 * @author msivak
 */
public class CardPayload {
    private CardTemplate cardTemplate;
    private boolean dockedDown = true;

    public CardPayload(CardTemplate cardTemplate) {
        this.cardTemplate = cardTemplate;
    }

    public CardTemplate getCardTemplate() {
        return cardTemplate;
    }

    public boolean isDockedDown() {
        return dockedDown;
    }

    public void setDockedDown(boolean dockedDown) {
        this.dockedDown = dockedDown;
    }
}
