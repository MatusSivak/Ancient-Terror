package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

/**
 * @author msivak
 */
public class ThePoliticianActionListener extends AbstractActionPhaseListener<ThePoliticianActionListener.ThePoliticianAction> {

    private static final Logger logger = LogManager.getLogger(ThePoliticianActionListener.class);

    public ThePoliticianActionListener() {
        name = "Transfer\nAction";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Another investigator\n" +
                "of your choice\n" +
                "may immediately perform\n" +
                "1 additional action.";
    }

    @Override
    protected ThePoliticianActionListener.ThePoliticianAction createAction() {
        return new ThePoliticianActionListener.ThePoliticianAction();
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/ICON_THE_POLITICIAN.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_POLITICIAN;
    }

    @Override
    protected boolean isDisabled() {
        int delayedCount = 0;
        for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
            if (investigator.getInfo().getInvestigatorId() == InvestigatorId.THE_POLITICIAN) {
                continue;
            }
            if (investigator.isDelayed()) {
                delayedCount++;
            }
        }
        if (delayedCount == ServicePlatform.get().getInvestigators().getOnBoardInvestigators().size()-1) {
            disabledReason = "Can't select anyone.";
            return true;
        }
        if (ServicePlatform.get().getInvestigators().getOnBoardInvestigators().size() == 1) {
            disabledReason = "There are no other investigators.";
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class ThePoliticianAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing politician action...");
            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(false);
            investigatorRestriction.addDisabledInvestigator(InvestigatorId.THE_POLITICIAN);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction)
                    .subscribe(this::onSelect);

        }

        private void onSelect(InvestigatorId investigatorId) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getBasicActionService().addFreeAction();
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getGameService().justPerformAction();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_POLITICIAN);
            if (ServicePlatform.get().getPerformedActions().canPerformAction(InvestigatorId.THE_POLITICIAN)) {
                // TODO make it as an action
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            }
            ServicePlatform.get().getInvestigatorService().convertTo(Void.class, () -> null);
            ServicePlatform.get().getService().release();

        }
    }
}
