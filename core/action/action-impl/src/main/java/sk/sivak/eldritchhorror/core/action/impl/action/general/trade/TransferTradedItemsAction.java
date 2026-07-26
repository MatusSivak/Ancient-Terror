package sk.sivak.eldritchhorror.core.action.impl.action.general.trade;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_TRAVEL_TICKETS;

public class TransferTradedItemsAction extends AbstractHookableAction<TradeData, TradeData> {

    public TransferTradedItemsAction() {
        super(BeforeAfterEvent.TRANSFER_TRADED_ITEMS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super TradeData> ss) {
        for (CardInfo cardInfo : input.getSourceCards()) {
            transferCard(cardInfo, input.getSourceInvestigatorId());
        }
        for (CardInfo cardInfo : input.getTargetCards()) {
            transferCard(cardInfo, input.getTargetInvestigatorId());
        }

        ServicePlatform.get().getCluePool().tradeClues(input.getSourceInvestigatorId(), input.getTargetInvestigatorId(),
                input.getSourceTokenTrading().getAfterTrade().getClues() - input.getSourceTokenTrading().getBeforeTrade().getClues());
        transferTokens(input.getSourceTokenTrading(), input.getSourceInvestigatorId(), input.getTargetInvestigatorId());
        transferTokens(input.getTargetTokenTrading(), input.getTargetInvestigatorId(), input.getSourceInvestigatorId());

        ServicePlatform.get().getGameController().updateHud().subscribe(() -> ss.onSuccess(input));
    }

    private void transferTokens(TradeData.TokenTrading tokenTrading, InvestigatorId investigatorId, InvestigatorId otherInvestigatorId) {
        if (tokenTrading == null || tokenTrading.getBeforeTrade() == null || tokenTrading.getAfterTrade() == null) {
            return;
        }
        InvestigatorWrite investigator = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId);
        InvestigatorWrite otherInvestigator = ServicePlatform.get().getInvestigators().getInvestigator(otherInvestigatorId);
        int ticketsOwned = investigator.getShipTickets() + investigator.getTrainTickets();
        TradeData.Tokens beforeTrade = tokenTrading.getBeforeTrade();
        TradeData.Tokens afterTrade = tokenTrading.getAfterTrade();

        int trainTicketsDifference = afterTrade.getTrainTickets() - beforeTrade.getTrainTickets();
        int shipTicketsDifference = afterTrade.getShipTickets() - beforeTrade.getShipTickets();

        if (trainTicketsDifference < 0) {
            investigator.setTrainTickets(investigator.getTrainTickets() + trainTicketsDifference);
        } else if (trainTicketsDifference > 0 && ticketsOwned + trainTicketsDifference + shipTicketsDifference <= MAX_TRAVEL_TICKETS) {
            investigator.setTrainTickets(investigator.getTrainTickets() + trainTicketsDifference);
        } else if (trainTicketsDifference > 0 && ticketsOwned + trainTicketsDifference + shipTicketsDifference > MAX_TRAVEL_TICKETS) {
            otherInvestigator.setTrainTickets(otherInvestigator.getTrainTickets() + trainTicketsDifference);
        }

        if (shipTicketsDifference < 0) {
            investigator.setShipTickets(investigator.getShipTickets() + shipTicketsDifference);
        } else if (shipTicketsDifference > 0 && ticketsOwned + trainTicketsDifference + shipTicketsDifference <= MAX_TRAVEL_TICKETS) {
            investigator.setShipTickets(investigator.getShipTickets() + shipTicketsDifference);
        } else if (shipTicketsDifference > 0 && ticketsOwned + trainTicketsDifference + shipTicketsDifference > MAX_TRAVEL_TICKETS) {
            otherInvestigator.setShipTickets(otherInvestigator.getShipTickets() + shipTicketsDifference);
        }
    }

    private void transferCard(CardInfo cardInfo, InvestigatorId newOwner) {
        if (cardInfo instanceof AssetInfo) {
            AssetInfo assetInfo = (AssetInfo) cardInfo;
            ServicePlatform.get().getAssetsDeck().changeOwner(assetInfo, newOwner);
            ServicePlatform.get().getAssetListenerProvider().getAssetListener(assetInfo.getId()).changeOwner(newOwner);
        } else if (cardInfo instanceof SpellInfo) {
            SpellInfo spellInfo = (SpellInfo) cardInfo;
            ServicePlatform.get().getSpellsDeck().changeOwner(spellInfo, newOwner);
            ServicePlatform.get().getSpellListenerProvider().getSpellListener(spellInfo).changeOwner(newOwner);
        } else if (cardInfo instanceof ArtifactInfo) {
            ArtifactInfo artifactInfo = (ArtifactInfo) cardInfo;
            ServicePlatform.get().getArtifactsDeck().changeOwner(artifactInfo, newOwner);
            ServicePlatform.get().getArtifactListenerProvider().getArtifactListener(artifactInfo.getId()).changeOwner(newOwner);
        } else {
            throw new IllegalArgumentException(cardInfo + " cannot be transfered");
        }
    }
}
