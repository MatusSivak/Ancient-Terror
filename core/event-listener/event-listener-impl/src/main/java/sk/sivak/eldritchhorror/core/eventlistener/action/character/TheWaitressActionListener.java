package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;

/**
 * @author msivak
 */
public class TheWaitressActionListener extends AbstractActionPhaseListener<TheWaitressActionListener.TheWaitressAction> {

    private static final Logger logger = LogManager.getLogger(TheWaitressActionListener.class);

    public TheWaitressActionListener() {
        name = "Gain\nSpell";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Test Lore-1.\nIf you pass,\ngain one Spell.";
    }

    @Override
    protected TheWaitressAction createAction() {
        return new TheWaitressAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_WAITRESS;
    }

    @Override
    protected boolean isDisabled() {
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/ICON_THE_WAITRESS.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheWaitressAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            ServicePlatform.get().getTestService().test(Stat.LORE, -1, 1).subscribe(testData -> {
                ServicePlatform.get().getService().hold();
                if (testData.isSuccessful()) {
                    ServicePlatform.get().getGameService().gainSpell();
                }
                ServicePlatform.get().getTokenService().convertToNull();
                ServicePlatform.get().getService().release();
            });
        }
    }
}
