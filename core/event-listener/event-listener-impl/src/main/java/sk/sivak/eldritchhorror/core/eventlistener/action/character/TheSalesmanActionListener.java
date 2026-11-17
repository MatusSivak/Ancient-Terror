package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TheSalesmanActionListener extends AbstractActionPhaseListener<TheSalesmanActionListener.CharacterAction> {
    public TheSalesmanActionListener() { name = "Make a\nDeal"; }
    @Override protected String getAdditionalInfo() { return null; }
    @Override protected String getGeneralDescription() { return getActiveInvestigator().getInfo().getActionText(); }
    @Override protected CharacterAction createAction() { return new CharacterAction(); }
    @Override protected boolean isVisible() { return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_SALESMAN; }
    @Override protected boolean isDisabled() {
        if (getInvestigators().getOnBoardInvestigators().size() < 2) {
            disabledReason = "No one to trade with.";
            return true;
        }
        disabledReason = "No one has Item or Trinket possessions to trade.";
        return partners().isEmpty();
    }
    @Override protected boolean isNotRecommended() { return false; }
    @Override protected ActionButtonData.ActionButtonId getActionButtonId() { return ActionButtonData.ActionButtonId.INVESTIGATOR; }
    @Override protected String getTexturePath() { return "investigator/THE_SALESMAN.png"; }

    private static boolean isTradeable(Set<AssetTrait> traits) {
        return traits != null && (traits.contains(AssetTrait.ITEM) || traits.contains(AssetTrait.TRINKET));
    }

    private static List<CardInfo> tradeableCards(InvestigatorId investigatorId) {
        List<CardInfo> result = new ArrayList<>();
        for (AssetInfo asset : ServicePlatform.get().getAssetDeck().getAssets(investigatorId))
            if (isTradeable(asset.getTraits())) result.add(asset);
        for (ArtifactInfo artifact : ServicePlatform.get().getArtifactsDeck().getArtifacts(investigatorId))
            if (isTradeable(artifact.getTraits())) result.add(artifact);
        return result;
    }

    private static void filterOutCardsWithSameId(List<CardInfo> sourceCards, List<CardInfo> targetCards) {
        Iterator<CardInfo> iterator = sourceCards.iterator();
        while (iterator.hasNext()) {
            CardInfo sourceCard = iterator.next();
            for (CardInfo targetCard : targetCards) {
                if (targetCard.getId().equals(sourceCard.getId())) {
                    iterator.remove();
                    targetCards.remove(targetCard);
                    break;
                }
            }
        }
    }

    private List<InvestigatorId> partners() {
        List<InvestigatorId> result = new ArrayList<>();
        for (InvestigatorRead investigator : getInvestigators().getOnBoardInvestigators()) {
            InvestigatorId id = investigator.getInfo().getInvestigatorId();
            if (id == InvestigatorId.THE_SALESMAN) continue;
            List<CardInfo> source = tradeableCards(InvestigatorId.THE_SALESMAN);
            List<CardInfo> target = tradeableCards(id);
            filterOutCardsWithSameId(source, target);
            if (!source.isEmpty() || !target.isEmpty()) result.add(id);
        }
        return result;
    }

    private static TradeData.TokenTrading emptyTokens() {
        TradeData.TokenTrading tokenTrading = new TradeData.TokenTrading();
        tokenTrading.setBeforeTrade(new TradeData.Tokens());
        return tokenTrading;
    }

    protected class CharacterAction extends AbstractActionPhaseAction {
        @Override public void execute() {
            InvestigatorRestriction restriction = new InvestigatorRestriction(true);
            restriction.addAllowedInvestigators(partners());
            restriction.addDisabledInvestigator(InvestigatorId.THE_SALESMAN);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(restriction).subscribe(this::trade);
        }

        private void trade(InvestigatorId targetId) {
            List<CardInfo> sourceCards = tradeableCards(InvestigatorId.THE_SALESMAN);
            List<CardInfo> targetCards = tradeableCards(targetId);
            filterOutCardsWithSameId(sourceCards, targetCards);
            TradeData tradeData = new TradeData();
            tradeData.setSourceInvestigatorId(InvestigatorId.THE_SALESMAN);
            tradeData.setSourceTokenTrading(emptyTokens());
            tradeData.setSourceCards(sourceCards);
            tradeData.setTargetInvestigatorId(targetId);
            tradeData.setTargetTokenTrading(emptyTokens());
            tradeData.setTargetCards(targetCards);
            ServicePlatform.get().getBasicActionService().psychicAction(tradeData);
        }
    }
}
