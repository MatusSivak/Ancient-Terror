package sk.sivak.eldritchhorror.core.eventlistener.action.basic;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.AfterActionPerformedData;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public class SkipActionListener extends AbstractActionPhaseListener<SkipActionListener.SkipAction> {

    private static final Logger logger = LogManager.getLogger(SkipActionListener.class);

    public SkipActionListener() {
        name = "Skip\nTurn";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "You will not perform any action this turn.";
    }

    @Override
    protected SkipAction createAction() {
        return new SkipAction();
    }

    @Override
    protected boolean isVisible() {
        return true;
    }

    @Override
    protected boolean isDisabled() {
        return false;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.SKIP;
    }

    @Override
    protected String getTexturePath() {
        return "action_button/skip.png";
    }

    public class SkipAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing skip action...");
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            ServicePlatform.get().getPerformedActions().setDontWantToPerformAction(activeInvestigatorId);
            /*
            AfterActionPerformedData afterActionPerformedData = new AfterActionPerformedData();
            afterActionPerformedData.setCanPerformAction(false);
            afterActionPerformedData.setSkipped(true);
            afterActionPerformedData.setLast(ServicePlatform.get().getInvestigators().isActiveLast());
            ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.AFTER_ACTION_PERFORMED, afterActionPerformedData);
            */
        }
    }

}
