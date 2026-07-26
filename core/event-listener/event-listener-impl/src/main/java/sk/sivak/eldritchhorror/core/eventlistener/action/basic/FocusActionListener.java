package sk.sivak.eldritchhorror.core.eventlistener.action.basic;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import static sk.sivak.eldritchhorror.core.constants.GameProperties.MAX_FOCUS_TOKENS;

/**
 * @author msivak
 */
public class FocusActionListener extends AbstractActionPhaseListener<FocusActionListener.FocusAction> {

    private static final Logger logger = LogManager.getLogger(FocusActionListener.class);

    public FocusActionListener() {
        name = "Focus";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isNotRecommended()) {
            return null;
        } else {
            return "+1 Focus";
        }
    }

    @Override
    protected String getGeneralDescription() {
        return "Gain a Focus token,\n" +
                "which you can spend\n" +
                "during a test\n" +
                "to reroll a die.";
    }

    @Override
    protected FocusAction createAction() {
        return new FocusAction();
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
        InvestigatorRead activeInvestigator = getActiveInvestigator();
        if (activeInvestigator.getFocusTokens() == MAX_FOCUS_TOKENS) {
            notRecommendedReason = "Focus already full.";
            return true;
        }
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.FOCUS;
    }

    @Override
    protected String getTexturePath() {
        return "action_button/focus.png";
    }

    protected class FocusAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing focus action...");
            ServicePlatform.get().getBasicActionService().focus();
        }
    }

}
