package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.artifact.DeVermisMysteriisArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
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
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class DeVermisMysteriisListener extends AbstractArtifactListener<DeVermisMysteriisArtifact> {

    private static final Logger logger = LogManager.getLogger(DeVermisMysteriisListener.class);
    private ActionListener actionListener;
    private InitActionButtonListener initActionButtonListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);
        initActionButtonListener = new InitActionButtonListener();
        getEventQueue().addDirectEventListener(initActionButtonListener, DirectEvent.INIT_ACTION_BUTTON);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

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
            return "DeVermisMysteriisListener - InitActionButton";
        }
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, initActionButtonListener, enableCardListener);
    }


    private class ActionListener extends AbstractActionPhaseListener<ActionListener.DeVermisMysteriisAction> {

        private boolean enoughSanity;

        private void init() {
            if (!isVisible()) {
                return;
            }
            ServicePlatform.get().getTokenService().canSpend(0,0,0,1).subscribe(spendData -> {
                enoughSanity = spendData.hasEnough();
            });
        }


        public ActionListener() {
            name = "De Vermis\n" +
                    "Mysteriis";
        }

        @Override
        protected String getAdditionalInfo() {
           return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore. If you pass,\n" +
                    "you may spend 1 Sanity to\n" +
                    "improve 1 Skill of your choice.";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/DE_VERMIS_MYSTERIIS.jpg";
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
        protected DeVermisMysteriisAction createAction() {
            return new DeVermisMysteriisAction();
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

        protected class DeVermisMysteriisAction extends AbstractActionPhaseAction implements CardInfoAware<DeVermisMysteriisArtifact> {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Test Lore to improve one Skill.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    testLore();
                });
            }

            private void testLore() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                ServicePlatform.get().getTestService().test(Stat.LORE,0, JUST_ONE).subscribe(this::afterTest);
                ServicePlatform.get().getService().release();
            }

            private void afterTest(TestData testData) {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }

                Question<Boolean> question = new Question<>();
                question.setTitle("Spend 1 Sanity to improve one Skill?");
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
                ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                ServicePlatform.get().getInvestigatorService().improveSkill(null);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }


            @Override
            public DeVermisMysteriisArtifact getCardInfo() {
                return getArtifactInfo();
            }
        }
    }
}
