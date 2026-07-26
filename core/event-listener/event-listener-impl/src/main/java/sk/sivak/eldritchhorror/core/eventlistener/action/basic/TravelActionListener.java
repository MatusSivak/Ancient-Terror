package sk.sivak.eldritchhorror.core.eventlistener.action.basic;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;

/**
 * @author msivak
 */
public class TravelActionListener extends AbstractActionPhaseListener<TravelActionListener.TravelAction> {

    private static final Logger logger = LogManager.getLogger(TravelActionListener.class);

    public TravelActionListener() {
        name = "Travel";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Move to any adjacent space.\n" +
                "Then you may spend any number of tickets,\n" +
                "moving one additional space along\n" +
                "a Train or Ship path.";
    }

    @Override
    protected TravelAction createAction() {
        return new TravelAction();
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
        return ActionButtonData.ActionButtonId.TRAVEL;
    }

    @Override
    protected String getTexturePath() {
        return "action_button/travel.png";
    }

    protected class TravelAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing travel action...");
            ServicePlatform.get().getBasicActionService().travel();
        }
    }

}
