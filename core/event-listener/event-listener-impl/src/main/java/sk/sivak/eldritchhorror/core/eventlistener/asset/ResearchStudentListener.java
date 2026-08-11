package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.function.Predicate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.asset.ResearchStudentAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class ResearchStudentListener extends AbstractAssetListener<ResearchStudentAsset> {

    private static final Logger logger = LogManager.getLogger(ResearchStudentListener.class);
    private ResearchStudentActionListener researchStudentActionListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        researchStudentActionListener = new ResearchStudentActionListener();
        getEventQueue().addBeforeEventListener(researchStudentActionListener, BeforeAfterEvent.FIND_ACTIONS);
        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(researchStudentActionListener, enableCardListener);
    }

    private class ResearchStudentActionListener extends AbstractActionPhaseListener<ResearchStudentActionListener.ResearchStudentAction> {

        public ResearchStudentActionListener() {
            name = "Research\nStudent";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Roll 1 die.\nOn a 5 or 6, gain 1 Clue.\nOn a 1, discard the nearest Clue on the game board.";
        }

        @Override
        protected String getTexturePath() {
            return "card/asset/RESEARCH_STUDENT.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
        return 0.5f;
    }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected ResearchStudentAction createAction() {
            return new ResearchStudentAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            if (!enabled) {
                disabledReason = "Action already performed this round";
                return true;
            }
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }

        protected class ResearchStudentAction extends AbstractActionPhaseAction implements CardInfoAware<ResearchStudentAsset> {

            private static final int MIN_SUCCESSFUL = 5;
            private static final int MAX_FAILED = 1;

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Roll 1 die.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    rollDie();
                });
            }

            private void rollDie() {
                RollData rollData = new RollData();
                rollData.setMinSuccessful(MIN_SUCCESSFUL);
                rollData.setMaxFailed(MAX_FAILED);
                Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
                singleDieResult.subscribe(this::resolveDiceRoll);
            }

            private void resolveDiceRoll(Integer result) {
                ServicePlatform.get().getService().hold();
                if (result >= MIN_SUCCESSFUL) {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                } else if (result <= MAX_FAILED) {
                    discardClue();
                }
                ServicePlatform.get().getService().disableCard(getAssetInfo());
                ServicePlatform.get().getInvestigatorService().convertToNull();
                ServicePlatform.get().getService().release();
            }

            private void discardClue() {
                LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                Predicate<LocationInfo> predicate = locationInfo -> ServicePlatform.get().getCluePool().isClueAt(locationInfo.getLocationId());
                FindNearestData nearestClue = ServicePlatform.get().getLocationMap().findNearest(currentLocationId, predicate);
                if (nearestClue == null) {
                    Question<Boolean> question = new Question<>();
                    question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
                    question.setTitle("There are no spawned Clues.");
                    ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                        ServicePlatform.get().getTokenService().convertToNull();
                    });
                } else {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().discardClue(nearestClue.getLocationId());
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                    ServicePlatform.get().getTokenService().convertToNull();
                    ServicePlatform.get().getService().release();
                }
            }

            @Override
            public ResearchStudentAsset getCardInfo() {
                return getAssetInfo();
            }
        }
    }
}
