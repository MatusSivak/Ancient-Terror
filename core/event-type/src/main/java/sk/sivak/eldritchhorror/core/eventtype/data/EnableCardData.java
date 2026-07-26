package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

/**
 * @author msivak
 */
public class EnableCardData {

    private CardInfo cardInfo;

    public EnableCardData(CardInfo cardInfo) {
        this.cardInfo = cardInfo;
    }

    public CardInfo getCardInfo() {
        return cardInfo;
    }

    @Override
    public String toString() {
        return "EnableCardData{" +
                "cardInfo=" + cardInfo +
                '}';
    }
}
