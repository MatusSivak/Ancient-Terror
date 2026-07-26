package sk.sivak.eldritchhorror.core.view.components.card;

/**
 * @author msivak
 */
public class CardDuplicator {
    public static CardTemplate duplicate(CardTemplate cardTemplate) {
        CardTemplate duplicate = null;
        if (cardTemplate.getAssetInfo() != null) {
            duplicate = CardTemplate.buildCard(cardTemplate.getAssetInfo());
        } else if (cardTemplate.getConditionInfo() != null) {
            duplicate = CardTemplate.buildCard(cardTemplate.getConditionInfo());
        } else if (cardTemplate.getSpellInfo() != null) {
            duplicate = CardTemplate.buildCard(cardTemplate.getSpellInfo());
        } else if (cardTemplate.getArtifactInfo() != null) {
            duplicate = CardTemplate.buildCard(cardTemplate.getArtifactInfo());
        } else if (cardTemplate.getTokenCardInfo() != null) {
            if (cardTemplate.getTokenCardInfo().isClue()) {
                duplicate = CardTemplate.buildClueCard();
            } else if (cardTemplate.getTokenCardInfo().isTrainTicket()) {
                duplicate = CardTemplate.buildTrainTicketCard();
            } else if (cardTemplate.getTokenCardInfo().isShipTicket()) {
                duplicate = CardTemplate.buildShipTicketCard();
            } else {
                throw new IllegalArgumentException("No idea what this token represents");
            }
        } else {
            throw new IllegalArgumentException("Duplicate cannot be null at this point");
        }
        duplicate.setScaleX(cardTemplate.getScaleX());
        duplicate.setScaleY(cardTemplate.getScaleY());
        return duplicate;
    }
}
