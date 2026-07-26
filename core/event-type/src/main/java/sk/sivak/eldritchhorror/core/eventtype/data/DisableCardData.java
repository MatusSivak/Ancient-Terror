package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

/**
 * @author msivak
 */
public class DisableCardData {

    private CardInfo cardInfo;

    public DisableCardData(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    @Override
    public String toString() {
        return "DisableCardData{" +
                "cardInfo=" + cardInfo +
                '}';
    }
}
