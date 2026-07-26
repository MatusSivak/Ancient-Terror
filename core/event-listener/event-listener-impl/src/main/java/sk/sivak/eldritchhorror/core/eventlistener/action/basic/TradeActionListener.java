package sk.sivak.eldritchhorror.core.eventlistener.action.basic;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

/**
 * @author msivak
 */
public class TradeActionListener extends AbstractActionPhaseListener<TradeActionListener.TradeAction> {

    private static final Logger logger = LogManager.getLogger(TradeActionListener.class);

    public TradeActionListener() {
        name = "Trade";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Trade your possessions\nwith another investigator\non the same space.";
    }

    @Override
    protected TradeAction createAction() {
        return new TradeAction();
    }

    @Override
    protected boolean isVisible() {
        return true;
    }

    @Override
    protected boolean isDisabled() {
        if (findOtherInvestigatorsOnThisSpace().isEmpty()) {
            disabledReason = "No other investigator on this space.";
            return true;
        } else {
            return false;
        }
    }

    private List<? extends InvestigatorRead> findOtherInvestigatorsOnThisSpace() {
        InvestigatorId activeInvestigatorId = getInvestigators().getActiveInvestigatorId();
        LocationId activeLocationId = ServicePlatform.get().getInvestigators().getInvestigator(activeInvestigatorId).getCurrentLocationId();
        List<? extends InvestigatorRead> selectedInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        return Stream.collectToList(selectedInvestigators,
                selected -> selected.getCurrentLocationId() == activeLocationId &&
                        selected.getInfo().getInvestigatorId() != activeInvestigatorId);
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.TRADE;
    }

    @Override
    protected String getTexturePath() {
        return "action_button/trade.png";
    }

    protected class TradeAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing trade action...");
            ServicePlatform.get().getBasicActionService().trade();
        }
    }

}
