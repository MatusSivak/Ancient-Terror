package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.SatchelOfTheVoidArtifact;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainCardData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SatchelOfTheVoidListener extends AbstractArtifactListener<SatchelOfTheVoidArtifact> {

    private static final Logger logger = LogManager.getLogger(SatchelOfTheVoidListener.class);
    private ActionListener actionListener;
    private EnableCardListener enableCardListener;
    private BeforeGainLostInTimeAndSpaceListener beforeGainLostInTimeAndSpaceListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, enableCardListener, beforeGainLostInTimeAndSpaceListener);
    }

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

        beforeGainLostInTimeAndSpaceListener = new BeforeGainLostInTimeAndSpaceListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeGainLostInTimeAndSpaceListener, BeforeAfterEvent.SELECT_CARD_TO_GAIN);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.SatchelOfTheVoidAction> {

        public ActionListener() {
            name = "Satchel of\nthe Void";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Look at the top Gate\n" +
                    "in the Gate stack.\n" +
                    "Gain 1 Clue if that Gate\n" +
                    "represents current Omen.\n";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/SATCHEL_OF_THE_VOID.jpg";
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
        protected SatchelOfTheVoidAction createAction() {
            return new SatchelOfTheVoidAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            if (ServicePlatform.get().getGateStackRead().getSpawnedGatesLocations().size() == 9) {
                disabledReason = "All Gates are spawned.";
                return true;
            }
            if (!enabled) {
                disabledReason = "Action already performed this round"; // THIS MUST BE LAST
                return true;
            }
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }

        protected class SatchelOfTheVoidAction extends AbstractActionPhaseAction {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Look at the top Gate.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    afterCardIsConfirmed();
                });
            }

            private void afterCardIsConfirmed() {
                GateInfo topGate = ServicePlatform.get().getGateStackRead().getTopGate();
                GateColor topGateColor = topGate.getGateColor();
                OmenColor omenColor = ServicePlatform.get().getOmenTrack().getCurrentOmen().getOmenColor();
                Question<Boolean> question = new Question<>();
                Question.SelectComponentsTableData<GateInfo> selectComponentsTableData = new Question.SelectComponentsTableData<>();
                selectComponentsTableData.setAvailableKeys(Collections.singletonList(topGate));
                selectComponentsTableData.setType(Question.SelectComponentsTableType.GATES);
                question.setSelectComponentsTableData(selectComponentsTableData);
                boolean value;
                if (omenColor.toGateColor().equals(topGateColor)) {
                    value = true;
                    question.setTitle("Gate ("+omenColor.getPrettyString()+") represents current Omen ("+omenColor.getPrettyString()+").");

                } else {
                    question.setTitle("Gate ("+topGateColor.getPrettyString()+") does not represent " +
                            "current Omen ("+omenColor.getPrettyString()+").");
                    value = false;
                }
                question.setOptions(Collections.singletonList(new Question.Option<>("OK", value)));
                ServicePlatform.get().getGameService().ask(question).subscribe(this::onAnswer);
            }

            private void onAnswer(Answer<Boolean, Object> answer) {
                ServicePlatform.get().getService().hold();
                if (answer.getResponseData()) {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                }
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }
        }
    }

    private class BeforeGainLostInTimeAndSpaceListener extends EventListenerImpl<GainCardData> {

        @Override
        public void onNotify(GainCardData eventData) {
            if (ConditionId.LOST_IN_TIME_AND_SPACE != eventData.getConditionId()) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != investigatorId) {
                return;
            }

            ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getArtifactInfo())
                    .withDisplayAssetResponseType(DisplayAssetResponseType.YES_NO)
                    .withAssetHideType(HideType.RETURN)
                    .withTitle("Prevent Lost in Time and Space?")
                    .build();
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(response -> onResponse(response, eventData));
        }

        private void onResponse(ShowCardResponse response, GainCardData eventData) {
            if (response == ShowCardResponse.YES) {
                ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.REGISTER_GAINED_CARD, null);
            } else {
                ServicePlatform.get().getService().convertTo(GainCardData.class, () -> eventData);
            }
        }

        @Override
        public Class<GainCardData> getDataClass() {
            return GainCardData.class;
        }
    }

}
