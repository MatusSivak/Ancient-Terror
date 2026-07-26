package sk.sivak.eldritchhorror.core.action.impl.action.general.trade;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectItemsForTradeAction extends AbstractHookableAction<InvestigatorId, TradeData> {


    public CollectItemsForTradeAction() {
        super(BeforeAfterEvent.COLLECT_ITEMS_FOR_TRADE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TradeData> ss) {
        TradeData tradeData = new TradeData();
        tradeData.setTargetInvestigatorId(input);
        ArrayList<CardInfo> targetCards = new ArrayList<>();
        targetCards.addAll(ServicePlatform.get().getAssetsDeck().getAssets(input));
        targetCards.addAll(ServicePlatform.get().getSpellsDeck().getSpells(input));
        targetCards.addAll(ServicePlatform.get().getArtifactsDeck().getArtifacts(input));
        tradeData.setTargetCards(targetCards);
        tradeData.setTargetTokenTrading(createOtherPossessionTrading(input));

        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        tradeData.setSourceInvestigatorId(activeInvestigatorId);
        ArrayList<CardInfo> sourceCards = new ArrayList<>();
        sourceCards.addAll(ServicePlatform.get().getAssetsDeck().getAssets(activeInvestigatorId));
        sourceCards.addAll(ServicePlatform.get().getArtifactsDeck().getArtifacts(activeInvestigatorId));
        sourceCards.addAll(ServicePlatform.get().getSpellsDeck().getSpells(activeInvestigatorId));
        tradeData.setSourceCards(sourceCards);
        tradeData.setSourceTokenTrading(createOtherPossessionTrading(activeInvestigatorId));

        filterOutCardsWithSameId(sourceCards, targetCards);
        ss.onSuccess(tradeData);
    }

    private void filterOutCardsWithSameId(List<CardInfo> sourceCards, List<CardInfo> targetCards) {
        Iterator<CardInfo> sourceCardsIterator = sourceCards.iterator();
        while (sourceCardsIterator.hasNext()) {
            CardInfo sourceCard = sourceCardsIterator.next();
            CardInfo cardWithSameId = findCardWithSameId(targetCards, sourceCard);
            if (cardWithSameId != null) {
                sourceCardsIterator.remove();
                targetCards.remove(cardWithSameId);
            }
        }
    }

    private CardInfo findCardWithSameId(List<CardInfo> targetCards, CardInfo cardInfo) {
        for (CardInfo targetCard : targetCards) {
            if (targetCard.getId().equals(cardInfo.getId())) {
                return targetCard;
            }
        }
        return null;
    }

    private TradeData.TokenTrading createOtherPossessionTrading(InvestigatorId investigatorId) {
        TradeData.TokenTrading tokenTrading = new TradeData.TokenTrading();
        tokenTrading.setBeforeTrade(new TradeData.Tokens());
        InvestigatorWrite investigator = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId);
        tokenTrading.getBeforeTrade().setShipTickets(investigator.getShipTickets());
        tokenTrading.getBeforeTrade().setTrainTickets(investigator.getTrainTickets());
        tokenTrading.getBeforeTrade().setClues(ServicePlatform.get().getCluePool().getClueCount(investigatorId));
        return tokenTrading;
    }
}
