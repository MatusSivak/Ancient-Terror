package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.artifact.SerpentCrownArtifact;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class SerpentCrownListener extends AbstractArtifactListener<SerpentCrownArtifact> {

    private static final Logger logger = LogManager.getLogger(SerpentCrownListener.class);
    private ActionListener actionListener;

    private InitActionButtonListener initActionButtonListener;
    private EnableCardListener enableCardListener;
    private ReckoningListener reckoningListener;

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);

        initActionButtonListener = new InitActionButtonListener();
        getEventQueue().addDirectEventListener(initActionButtonListener, DirectEvent.INIT_ACTION_BUTTON);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

        reckoningListener = new ReckoningListener();
        getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_CARDS_2);
    }

    private class InitActionButtonListener extends EventListenerImpl<Void> {
        @Override
        public void onNotify(Void eventData) {
            actionListener.init();
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }

        @Override
        public String toString() {
            return "SerpentCrownListener - InitActionButton";
        }
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, initActionButtonListener, enableCardListener, reckoningListener);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.SerpentCrownAction> {

        private boolean enoughSanity;

        public ActionListener() {
            name = "Serpent\n" +
                    "Crown";
        }

        private void init() {
            if (!isVisible()) {
                return;
            }
            ServicePlatform.get().getTokenService().canSpend(0,0,0,1).subscribe(spendData -> {
                enoughSanity = spendData.hasEnough();
            });
        }

        @Override
        protected String getAdditionalInfo() {
           return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Will-1. If you pass,\n" +
                    "you may spend 1 Sanity\n" +
                    "to gain a random Ally.";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/SERPENT_CROWN.jpg";
        }

        @Override
        protected boolean getNeedsScaleDown() {
            return true;
        }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected SerpentCrownAction createAction() {
            return new SerpentCrownAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            if (!enoughSanity) {
                disabledReason = "You do not have enough Sanity.";
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

        protected class SerpentCrownAction extends AbstractActionPhaseAction implements CardInfoAware<SerpentCrownArtifact> {

            @Override
            public void execute() {
                ServicePlatform.get().getTokenService().canSpend(0,0,0,1).subscribe(this::onCanSpend);
            }

            private void onCanSpend(SpendData spendData) {
                if (!spendData.hasEnough()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Test Will to gain an Ally.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    testWill();
                });
            }

            private void testWill() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                ServicePlatform.get().getTestService().test(Stat.WILL,-1, JUST_ONE).subscribe(this::afterTest);
                ServicePlatform.get().getService().release();
            }

            private void afterTest(TestData testData) {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }

                Question<Boolean> question = new Question<>();
                question.setTitle("Spend 1 Sanity to gain a random Ally?");
                question.addOption(new Question.Option<>("No", false, 0x800000ff))
                        .addOption(new Question.Option<>("Yes", true, 0x008000ff));
                ServicePlatform.get().getGameService().ask(question).subscribe(this::onAnswer);
            }

            private void onAnswer(Answer<Boolean, Object> answer) {
                if (!answer.getResponseData()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ServicePlatform.get().getTokenService().spend(0,0,0,1).subscribe(this::onSpend);
            }

            private void onSpend(SpendData spendData) {
                if (!spendData.hasEnough()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ServicePlatform.get().getService().hold();
                spendData.pay();
                ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.ALLY));
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }


            @Override
            public SerpentCrownArtifact getCardInfo() {
                return getArtifactInfo();
            }
        }
    }

    private class ReckoningListener extends AbstractCardReckoningListener {

        @Override
        protected InvestigatorId getCardOwnerId() {
            return investigatorId;
        }

        @Override
        protected boolean shouldTrigger() {
            List<? extends CardInfo> allies = EncounterUtils.getPossession(investigatorId, AssetTrait.ALLY);
            return !allies.isEmpty();
        }

        @Override
        protected void onNotify() {
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getArtifactInfo())
                    .withTitle("Discard one Ally.")
                    .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                    .build();
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onConfirm());
        }

        private void onConfirm() {
            List<? extends CardInfo> allies = EncounterUtils.getPossession(investigatorId, AssetTrait.ALLY);
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setTitleText("Select Ally to discard");
            selectCardData.setHideText("Show Allies?");
            selectCardData.setAvailableCards(allies);
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(cardInfo -> {
                discardAlly((AssetInfo)cardInfo);
            });
        }

        private void discardAlly(AssetInfo ally) {
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(ally);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setTitle("Discard Ally");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displayAsset.subscribe(response -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().discardAssetFromInvestigator(activeInvestigatorId, ally, true);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            });
        }

    }

}
