package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;

import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class TheShamanActionListener extends AbstractActionPhaseListener<TheShamanActionListener.TheShamanAction> {

    private static final Logger logger = LogManager.getLogger(TheShamanActionListener.class);

    public TheShamanActionListener() {
        name = "Look at\nGates";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Look at the top 2 Gates\n" +
                "in the Gate stack.\n" +
                "Put 1 Gate on the top\n" +
                "of the Gate stack,\n" +
                "and the other on the bottom.";
    }

    @Override
    protected TheShamanAction createAction() {
        return new TheShamanAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_SHAMAN;
    }

    @Override
    protected boolean isDisabled() {
        if (ServicePlatform.get().getGateStackRead().getSpawnedGatesLocations().size() == 8) {
            disabledReason = "There is only one Gate remaining.";
            return true;
        }
        if (ServicePlatform.get().getGateStackRead().getSpawnedGatesLocations().size() == 9) {
            disabledReason = "All Gates are spawned.";
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
        return "investigator/THE_SHAMAN.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheShamanAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing the shaman action...");
            List<GateInfo> topTwoGates = new LinkedList<>(ServicePlatform.get().getGateStackRead().getTopTwoGates());
            SelectSingleGateData selectSingleGateData = new SelectSingleGateData(topTwoGates);
            selectSingleGateData.setTitleText("Select top Gate");
            ServicePlatform.get().getGameService().selectSingleGate(selectSingleGateData).subscribe(selectedGate -> {
                topTwoGates.remove(selectedGate);
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().putGateToTop(selectedGate);
                ServicePlatform.get().getInvestigatorService().putGateToBottom(topTwoGates.get(0));
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            });

        }


    }
}
