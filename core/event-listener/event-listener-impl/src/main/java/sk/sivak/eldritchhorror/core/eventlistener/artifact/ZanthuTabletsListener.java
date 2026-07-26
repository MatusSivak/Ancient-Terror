package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.artifact.ZanthuTabletsArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetEventListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.ArcaneManuscriptsListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class ZanthuTabletsListener extends AbstractArtifactListener<ZanthuTabletsArtifact> {

    private static final Logger logger = LogManager.getLogger(ZanthuTabletsListener.class);
    private ActionListener actionListener;
    private InitActionButtonListener initActionButtonListener;
    private EnableCardListener enableCardListener;
    private RegisterUsableAssetListener registerUsableAssetListener;

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);

        initActionButtonListener = new InitActionButtonListener();
        getEventQueue().addDirectEventListener(initActionButtonListener, DirectEvent.INIT_ACTION_BUTTON);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
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
            return "ZanthuTabletsListener - InitActionButton";
        }
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, initActionButtonListener, enableCardListener, registerUsableAssetListener);
    }


    private class ActionListener extends AbstractActionPhaseListener<ActionListener.ZanthuTabletsAction> {

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
            name = "Zanthu\n" +
                    "Tablets";
        }

        @Override
        protected String getAdditionalInfo() {
           return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore. If you pass,\n" +
                    "you may spend 1 Sanity to\n" +
                    "gain 2 Spells,\n" +
                    "then discard 1 Spell.";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/ZANTHU_TABLETS.jpg";
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
        protected ZanthuTabletsAction createAction() {
            return new ZanthuTabletsAction();
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

        protected class ZanthuTabletsAction extends AbstractActionPhaseAction implements CardInfoAware<ZanthuTabletsArtifact> {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Test Lore to gain 2 Spells.");
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
                question.setTitle("Spend 1 Sanity to gain 2 spells?");
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
                ServicePlatform.get().getGameService().gainSpell();
                ServicePlatform.get().getGameService().gainSpell();
                ServicePlatform.get().getService().addEventCommand(in -> {
                    discardOneSpells();
                });

                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }


            @Override
            public ZanthuTabletsArtifact getCardInfo() {
                return getArtifactInfo();
            }
        }

        private void discardOneSpells() {
            InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
            List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(activeInvestigatorId);
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setAvailableCards(spells);
            selectCardData.setHideText("Display Spells?");
            selectCardData.setTitleText("Select Spell to discard");
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
        }
    }

    private class RegisterUsableAssetListener extends AbstractArtifactEventListener<TestData> {

        private boolean disable;

        public RegisterUsableAssetListener() {
            super(ZanthuTabletsListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.LORE) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.SPELL)) {
                    return;
                }
                if (getArtifactInfo().isDisabled()) {
                    getArtifactInfo().enable();
                    disable = true;
                }
                addUsableAsset(input, getArtifactInfo(), 3, 0);
                ServicePlatform.get().getEventQueue().addDirectEventListener(new EventListener<TestData>() {

                    @Override
                    public void onNotify(TestData eventData) {
                        if (disable) {
                            getArtifactInfo().disable();
                        }
                        ServicePlatform.get().getEventQueue().unregisterListener(this);
                    }

                    @Override
                    public Class<TestData> getDataClass() {
                        return TestData.class;
                    }
                }, DirectEvent.REGISTER_BONUS_DICE);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
