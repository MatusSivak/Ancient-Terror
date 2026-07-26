package sk.sivak.eldritchhorror.core.eventtype.data.card;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

public class GainedCardData {
    private CardInfo cardToGain;
    private CardType cardType;
    private boolean confirmRequired;

    public GainedCardData(CardInfo cardToGain, CardType cardType, boolean confirmRequired) {
        this.cardToGain = cardToGain;
        this.cardType = cardType;
        this.confirmRequired = confirmRequired;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public <T extends CardInfo> T getCardToGain() {
        return (T) cardToGain;
    }

    public void setCardToGain(CardInfo cardToGain) {
        this.cardToGain = cardToGain;
    }

    public boolean isConfirmRequired() {
        return confirmRequired;
    }

    public enum CardType {
        ASSET,
        CONDITION,
        SPELL,
        ARTIFACT
    }
}
