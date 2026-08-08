package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.model.ConditionsDeckRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class TheBootleggerActionListener extends AbstractActionPhaseListener<TheBootleggerActionListener.TheBootleggerAction> {

    private static final Logger logger = LogManager.getLogger(TheBootleggerActionListener.class);

    public TheBootleggerActionListener() {
        name = "Bootlegger\nAction";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "You and/or another investigator " +
                "on your space may move one space along a Ship or Train path.";
    }

    @Override
    protected TheBootleggerAction createAction() {
        return new TheBootleggerAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_BOOTLEGGER;
    }

    @Override
    protected boolean isDisabled() {
        if (findMovableInvestigators().isEmpty()) {
            disabledReason = "No one can move";
            return true;
        }
        if (findConnections().isEmpty()) {
            disabledReason = "Nowhere no move.";
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
        return "investigator/ICON_THE_BOOTLEGGER.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheBootleggerAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            boolean canActiveInvestigatorMove = !ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DETAINED);

            if (canActiveInvestigatorMove) {
                if (findMovableInvestigators().size() == 1) {
                    moveActiveInvestigator();
                } else {
                    startAskingQuestions();
                }
            } else {
                selectAndMoveAnotherInvestigator();
            }

        }

        private void moveActiveInvestigator() {
            TravelData travelData = new TravelData(false);
            travelData.setConnectionRestrictions(findConnections());

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getBasicActionService().moveSpaces(travelData, 1);
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private void selectAndMoveAnotherInvestigator() {
            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
            investigatorRestriction.addAllowedInvestigators(findOtherMovableInvestigators());
            ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction).subscribe(investigatorId -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                moveActiveInvestigator();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_BOOTLEGGER);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            });
        }

        private void startAskingQuestions() {
            Question<Boolean> question = new Question<>();
            question.setTitle("Do you want to travel?");
            question.setPortraitBeforeTitle(InvestigatorId.THE_BOOTLEGGER);
            question.setOptions(Question.Option.noYesOptions);
            ServicePlatform.get().getGameService().ask(question).subscribe(wantsToTravel -> {
                if (wantsToTravel.getResponseData()) {
                    Question<Boolean> question2 = new Question<>();
                    question2.setTitle("Take another investigator with you?");
                    question2.setPortraitBeforeTitle(InvestigatorId.THE_BOOTLEGGER);
                    if (findOtherMovableInvestigators().size() > 1) {
                        Question.SelectComponentsTableData<InvestigatorId> selectComponentsTableData = new Question.SelectComponentsTableData<>();
                        selectComponentsTableData.setType(Question.SelectComponentsTableType.INVESTIGATORS);
                        selectComponentsTableData.setAvailableKeys(findOtherMovableInvestigators());
                        question2.setSelectComponentsTableData(selectComponentsTableData);
                    }
                    question2.setOptions(Question.Option.noYesOptions);
                    ServicePlatform.get().getGameService().<Boolean, InvestigatorId>ask(question2).subscribe(takeAnotherInvestigator -> {
                        if (takeAnotherInvestigator.getResponseData() && takeAnotherInvestigator.getAdditionalData() == null) {
                            moveWithAnotherInvestigator();
                        } else if (takeAnotherInvestigator.getResponseData() && takeAnotherInvestigator.getAdditionalData() != null) {
                            moveWithAnotherInvestigator(takeAnotherInvestigator.getAdditionalData());
                        } else {
                            moveActiveInvestigator();
                        }
                    });
                } else {
                    selectAndMoveAnotherInvestigator();
                }
            });
        }

        private void moveWithAnotherInvestigator() {
            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
            investigatorRestriction.addAllowedInvestigators(findOtherMovableInvestigators());
            ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction).subscribe(this::moveWithAnotherInvestigator);
        }

        private void moveWithAnotherInvestigator(InvestigatorId anotherInvestigatorId) {
            List<LocationId> availableLocations = Stream.collectToList(Stream.map(findConnections(), LocationInfo.Connection::getLocationId));
            ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(availableLocations)).subscribe(locationId -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().removeInvestigatorFromPlace(InvestigatorId.THE_BOOTLEGGER);
                ServicePlatform.get().getInvestigatorService().removeInvestigatorFromPlace(anotherInvestigatorId);
                ServicePlatform.get().getInvestigatorService().spawnInvestigator(InvestigatorId.THE_BOOTLEGGER, locationId);
                ServicePlatform.get().getInvestigatorService().spawnInvestigator(anotherInvestigatorId, locationId);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            });
        }

    }

    private List<LocationInfo.Connection> findConnections() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        return Stream.collectToList(ServicePlatform.get().getLocationMap().getLocationInfo(currentLocationId).getConnections(),
                connection -> connection.getPathType() == PathType.SHIP || connection.getPathType() == PathType.TRAIN);
    }

    private List<? extends InvestigatorRead> findMovableInvestigators() {
        ConditionsDeckRead conditionsDeck = ServicePlatform.get().getConditionsDeck();
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        List<? extends InvestigatorRead> sameLocationInvestigators = Stream.collectToList(investigators,
                investigator -> investigator.getCurrentLocationId() == getActiveInvestigator().getCurrentLocationId());
        return Stream.collectToList(sameLocationInvestigators,
                investigator -> !conditionsDeck.hasCondition(investigator.getInfo().getInvestigatorId(), ConditionId.DETAINED));
    }

    private List<InvestigatorId> findOtherMovableInvestigators() {
        List<? extends InvestigatorRead> movableInvestigators = findMovableInvestigators();
        IterableUtils.removeIf(movableInvestigators,
                investigator -> investigator.getInfo().getInvestigatorId() == InvestigatorId.THE_BOOTLEGGER);
        return Stream.collectToList(Stream.map(movableInvestigators,
                investigator -> investigator.getInfo().getInvestigatorId()));
    }
}
