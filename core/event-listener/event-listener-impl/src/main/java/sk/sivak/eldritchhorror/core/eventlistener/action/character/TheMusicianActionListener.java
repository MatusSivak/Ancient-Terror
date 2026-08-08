package sk.sivak.eldritchhorror.core.eventlistener.action.character;

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
public class TheMusicianActionListener extends AbstractActionPhaseListener<TheMusicianActionListener.TheMusicianAction> {

    private static final Logger logger = LogManager.getLogger(TheMusicianActionListener.class);

    public TheMusicianActionListener() {
        name = "Recover\nSanity";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isNotRecommended()) {
            return null;
        }
        InvestigatorRead activeInvestigator = getInvestigators().getActiveInvestigator();
        boolean canActiveGainSanity = activeInvestigator.getCurrentSanity() < activeInvestigator.getInfo().getMaxSanity();

        StringBuilder sb = new StringBuilder();

        if (canActiveGainSanity) {
            sb.append(activeInvestigator.getInfo().getInvestigatorId()).append("\n");
        }
        for (InvestigatorRead otherInvestigator : findOtherInvestigatorsOnThisSpace()) {
            sb.append(otherInvestigator.getInfo().getInvestigatorId()).append("\n");
        }
        return sb.toString();
    }

    @Override
    protected String getGeneralDescription() {
        return "Each investigator on your space recovers 1 sanity.";
    }

    @Override
    protected TheMusicianAction createAction() {
        return new TheMusicianAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_MUSICIAN;
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
        return "investigator/ICON_THE_MUSICIAN.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        InvestigatorRead activeInvestigator = getInvestigators().getActiveInvestigator();
        boolean canActiveGainSanity = activeInvestigator.getCurrentSanity() < activeInvestigator.getInfo().getMaxSanity();
        if (findOtherInvestigatorsOnThisSpace().isEmpty() && !canActiveGainSanity) {
            notRecommendedReason = "Sanity of Investigators on this space is full.";
            return true;
        }
        return false;
    }

    private List<? extends InvestigatorRead> findOtherInvestigatorsOnThisSpace() {
        InvestigatorId activeInvestigatorId = getInvestigators().getActiveInvestigatorId();
        LocationId activeLocationId = ServicePlatform.get().getInvestigators().getInvestigator(activeInvestigatorId).getCurrentLocationId();
        List<? extends InvestigatorRead> selectedInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        return Stream.collectToList(selectedInvestigators,
                selected -> (selected.getCurrentSanity() < selected.getInfo().getMaxSanity() &&
                        (selected.getCurrentLocationId() == activeLocationId) &&
                        selected.getInfo().getInvestigatorId() != activeInvestigatorId));
    }

    protected class TheMusicianAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing the musician action...");
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainSanity(1);
            boolean investigatorChanged = false;
            for (InvestigatorRead investigatorRead : findOtherInvestigatorsOnThisSpace()) {
                investigatorChanged = true;
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorRead.getInfo().getInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getTokenService().gainSanity(1);
            }
            if (investigatorChanged) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_MUSICIAN);
                ServicePlatform.get().getService().addEventCommand(in -> {
                    if (ServicePlatform.get().getPerformedActions().canPerformAction(InvestigatorId.THE_MUSICIAN)) {
                        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    }
                });
            }
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        }


    }
}
