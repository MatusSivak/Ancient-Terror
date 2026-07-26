package sk.sivak.eldritchhorror.core.constants;

import sk.sivak.eldritchhorror.core.constants.card.CardId;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.card.Trait;

import java.util.Set;

public class TokenCardInfo implements CardInfo {
    private String name;
    private boolean clue;
    private boolean trainTicket;
    private boolean shipTicket;

    private TokenCardInfo(String name) {
        this.name = name;
    }

    public static TokenCardInfo buildClueTokenCardInfo() {
        TokenCardInfo clueCardInfo = new TokenCardInfo("Clue");
        clueCardInfo.clue = true;
        return clueCardInfo;
    }

    public static TokenCardInfo buildTrainTicketTokenCardInfo() {
        TokenCardInfo clueCardInfo = new TokenCardInfo("Train Ticket");
        clueCardInfo.trainTicket = true;
        return clueCardInfo;
    }

    public static TokenCardInfo buildShipTicketTokenCardInfo() {
        TokenCardInfo clueCardInfo = new TokenCardInfo("Ship Ticket");
        clueCardInfo.shipTicket = true;
        return clueCardInfo;
    }

    public boolean isClue() {
        return clue;
    }

    public boolean isTrainTicket() {
        return trainTicket;
    }

    public boolean isShipTicket() {
        return shipTicket;
    }

    @Override
    public CardId getId() {
        return new CardId() {
            @Override
            public String toString() {
                return name;
            }

            @Override
            public String asString() {
                return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            }
        };
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<? extends Trait> getTraits() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean hasReckoning() {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
