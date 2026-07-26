package sk.sivak.eldritchhorror.core.constants.trade;

import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.List;

public class TradeData {
    private InvestigatorId sourceInvestigatorId;
    private InvestigatorId targetInvestigatorId;

    private List<CardInfo> sourceCards;
    private List<CardInfo> targetCards;

    private TokenTrading sourceTokenTrading;
    private TokenTrading targetTokenTrading;

    public InvestigatorId getSourceInvestigatorId() {
        return sourceInvestigatorId;
    }

    public void setSourceInvestigatorId(InvestigatorId sourceInvestigatorId) {
        this.sourceInvestigatorId = sourceInvestigatorId;
    }

    public InvestigatorId getTargetInvestigatorId() {
        return targetInvestigatorId;
    }

    public void setTargetInvestigatorId(InvestigatorId targetInvestigatorId) {
        this.targetInvestigatorId = targetInvestigatorId;
    }

    public List<CardInfo> getSourceCards() {
        return sourceCards;
    }

    public void setSourceCards(List<CardInfo> sourceCards) {
        this.sourceCards = sourceCards;
    }

    public List<CardInfo> getTargetCards() {
        return targetCards;
    }

    public void setTargetCards(List<CardInfo> targetCards) {
        this.targetCards = targetCards;
    }

    public TokenTrading getSourceTokenTrading() {
        return sourceTokenTrading;
    }

    public void setSourceTokenTrading(TokenTrading sourceTokenTrading) {
        this.sourceTokenTrading = sourceTokenTrading;
    }

    public TokenTrading getTargetTokenTrading() {
        return targetTokenTrading;
    }

    public void setTargetTokenTrading(TokenTrading targetTokenTrading) {
        this.targetTokenTrading = targetTokenTrading;
    }

    @Override
    public String toString() {
        return "TradeData{" +
                "sourceInvestigatorId=" + sourceInvestigatorId +
                ", targetInvestigatorId=" + targetInvestigatorId +
                ", sourceCards=" + sourceCards +
                ", targetCards=" + targetCards +
                '}';
    }

    public static class TokenTrading {
        private Tokens beforeTrade;
        private Tokens afterTrade;

        public Tokens getBeforeTrade() {
            return beforeTrade;
        }

        public void setBeforeTrade(Tokens beforeTrade) {
            this.beforeTrade = beforeTrade;
        }

        public Tokens getAfterTrade() {
            return afterTrade;
        }

        public void setAfterTrade(Tokens afterTrade) {
            this.afterTrade = afterTrade;
        }
    }

    public static class Tokens {
        private int clues;
        private int trainTickets;
        private int shipTickets;

        public int getClues() {
            return clues;
        }

        public void setClues(int clues) {
            this.clues = clues;
        }

        public int getTrainTickets() {
            return trainTickets;
        }

        public void setTrainTickets(int trainTickets) {
            this.trainTickets = trainTickets;
        }

        public int getShipTickets() {
            return shipTickets;
        }

        public void setShipTickets(int shipTickets) {
            this.shipTickets = shipTickets;
        }
    }
}
