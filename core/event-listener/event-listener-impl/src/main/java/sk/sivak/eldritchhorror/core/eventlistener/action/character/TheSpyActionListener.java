package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.CluePoolRead;

/**
 * @author msivak
 */
public class TheSpyActionListener extends AbstractActionPhaseListener<TheSpyActionListener.TheSpyAction> {

    private static final Logger logger = LogManager.getLogger(TheSpyActionListener.class);

    public TheSpyActionListener() {
        name = "Gain\nClue";
    }

    @Override
    protected String getAdditionalInfo() {
        if (!isDisabled()) {
            return "+1 Clue";
        }
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "If you do not have\n" +
                "any Clues, gain 1 Clue";
    }

    @Override
    protected TheSpyActionListener.TheSpyAction createAction() {
        return new TheSpyActionListener.TheSpyAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_SPY;
    }

    @Override
    protected boolean isDisabled() {
        CluePoolRead cluePool = ServicePlatform.get().getCluePool();
        int clueCount = cluePool.getClueCount(getActiveInvestigator().getInfo().getInvestigatorId());
        if (clueCount > 0) {
            disabledReason = "You have at least 1 Clue.";
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
        return "investigator/THE_SPY.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheSpyAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing spy action...");
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getTokenService().convertToNull();
            ServicePlatform.get().getService().release();

        }
    }
}
