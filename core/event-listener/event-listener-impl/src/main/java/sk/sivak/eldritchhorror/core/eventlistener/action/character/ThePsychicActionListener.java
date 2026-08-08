package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class ThePsychicActionListener extends AbstractActionPhaseListener<ThePsychicActionListener.ThePsychicAction> {

    private static final Logger logger = LogManager.getLogger(ThePsychicActionListener.class);

    public ThePsychicActionListener() {
        name = "Trade\nClues";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
            if (investigator.getInfo().getInvestigatorId() == InvestigatorId.THE_PSYCHIC) {
                continue;
            }
            sb.append(investigator.getInfo().getInvestigatorId().toString());
            sb.append(" (");
            sb.append(ServicePlatform.get().getCluePool().getClueCount(investigator.getInfo().getInvestigatorId()));
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    protected String getGeneralDescription() {
        return "You may trade\n" +
                "any number of Clues\n" +
                "with an investigator\n" +
                "on any space.";
    }

    @Override
    protected ThePsychicAction createAction() {
        return new ThePsychicAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_PSYCHIC;
    }

    @Override
    protected boolean isDisabled() {
        if (ServicePlatform.get().getInvestigators().getOnBoardInvestigators().size() < 2) {
            disabledReason = "No one to trade with.";
            return true;
        }
        int ownedClues = 0;
        for (InvestigatorRead onBoardInvestigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
            ownedClues += ServicePlatform.get().getCluePool().getClueCount(onBoardInvestigator.getInfo().getInvestigatorId());
        }
        if (ownedClues == 0) {
            disabledReason = "No one owns any Clue.";
            return true;
        }
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/ICON_THE_PSYCHIC.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class ThePsychicAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            InvestigatorRestriction restriction = new InvestigatorRestriction(true);
            restriction.addDisabledInvestigator(InvestigatorId.THE_PSYCHIC);
            if (!hasClue(InvestigatorId.THE_PSYCHIC)) {
                // select only investigators that have clue and not me
                for (InvestigatorRead otherInvestigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                    if (hasClue(otherInvestigator.getInfo().getInvestigatorId())) {
                        continue;
                    }
                    restriction.addDisabledInvestigator(otherInvestigator.getInfo().getInvestigatorId());
                }
            }
            ServicePlatform.get().getInvestigatorService().selectInvestigator(restriction)
                    .subscribe(this::tradeWithSelectedInvestigator);

            logger.info("Executing psychic action...");
        }

        private void tradeWithSelectedInvestigator(InvestigatorId targetInvestigatorId) {
            int sourceClues = ServicePlatform.get().getCluePool().getClueCount(InvestigatorId.THE_PSYCHIC);
            int targetClues = ServicePlatform.get().getCluePool().getClueCount(targetInvestigatorId);
            TradeData tradeData = new TradeData();

            tradeData.setSourceInvestigatorId(InvestigatorId.THE_PSYCHIC);
            TradeData.TokenTrading sourceTokenTrading = new TradeData.TokenTrading();
            TradeData.Tokens sourceBeforeTrade = new TradeData.Tokens();
            sourceBeforeTrade.setClues(sourceClues);
            sourceTokenTrading.setBeforeTrade(sourceBeforeTrade);
            tradeData.setSourceTokenTrading(sourceTokenTrading);

            tradeData.setTargetInvestigatorId(targetInvestigatorId);
            TradeData.TokenTrading targetTokenTrading = new TradeData.TokenTrading();
            TradeData.Tokens targetBeforeTrade = new TradeData.Tokens();
            targetBeforeTrade.setClues(targetClues);
            targetTokenTrading.setBeforeTrade(targetBeforeTrade);
            tradeData.setTargetTokenTrading(targetTokenTrading);

            tradeData.setSourceCards(Collections.emptyList());
            tradeData.setTargetCards(Collections.emptyList());

            ServicePlatform.get().getBasicActionService().psychicAction(tradeData);
        }
    }

    private boolean hasClue(InvestigatorId investigatorId) {
        return ServicePlatform.get().getCluePool().getClueCount(investigatorId) > 0;
    }
}
