package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

/**
 * @author msivak
 */
public class TheViolinistActionListener extends AbstractActionPhaseListener<TheViolinistActionListener.TheViolinistAction> {

    private static final Logger logger = LogManager.getLogger(TheViolinistActionListener.class);

    private boolean enoughCluesOrFocus = false;

    public void registerInitActionButtonListener() {
        ServicePlatform.get().getEventQueue().addDirectEventListener(
                new InitActionButtonListener(), DirectEvent.INIT_ACTION_BUTTON);
    }

    private class InitActionButtonListener extends EventListenerImpl<Void> {
        @Override
        public void onNotify(Void eventData) {
            init();
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }

        @Override
        public String toString() {
            return "TheViolinist - InitActionButton";
        }
    }

    private void init() {
        if (!isVisible()) {
            return;
        }
        ServicePlatform.get().getTokenService().canSpend(1,1,0,0).subscribe(spendData -> {
            enoughCluesOrFocus = spendData.hasEnough();
        });
    }

    public TheViolinistActionListener() {
        name = "Improve\nSkill";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "You may spend\n" +
                "1 Clue and 1 Focus\n" +
                "to improve one skill\n" +
                "of your choice.";
    }

    @Override
    protected TheViolinistAction createAction() {
        return new TheViolinistAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_VIOLINIST;
    }

    @Override
    protected boolean isDisabled() {
        if (!enoughCluesOrFocus) {
            if (getActiveInvestigator().getFocusTokens() <= 0) {
                disabledReason = "You don't have a Focus.";
            } else {
                disabledReason = "You don't have a Clue.";
            }
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
        return "investigator/ICON_THE_VIOLINIST.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        int sum = 0;
        for (Integer value : getActiveInvestigator().getStatBonusMap().values()) {
            sum += value == null ? 0 : value;
        }
        if (sum == 10) {
            notRecommendedReason = "All Skills maxed.";
            return true;
        }
        return false;
    }

    protected class TheViolinistAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().spend(1,1,0,0).subscribe(spendData -> {

                if (!spendData.hasEnough()) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getBasicActionService().addFreeAction();
                    ServicePlatform.get().getBasicActionService().removePerformedAction(TheViolinistAction.this);
                    ServicePlatform.get().getService().convertToNull();
                    ServicePlatform.get().getService().release();
                    return;
                }
                ServicePlatform.get().getService().hold();
                spendData.pay();
                ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                ServicePlatform.get().getInvestigatorService().improveSkill(null);
                ServicePlatform.get().getTokenService().convertToNull();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        }
    }
}
