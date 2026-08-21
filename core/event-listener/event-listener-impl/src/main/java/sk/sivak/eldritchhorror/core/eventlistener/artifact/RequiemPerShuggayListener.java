package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.RequiemPerShuggayArtifact;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.CatBurglarListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.AbstractCardReckoningListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.GateStackRead;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class RequiemPerShuggayListener extends AbstractArtifactListener<RequiemPerShuggayArtifact> {

    private static final Logger logger = LogManager.getLogger(CatBurglarListener.class);
    private ActionListener actionListener;
    private InitActionButtonListener initActionButtonListener;
    private EnableCardListener enableCardListener;
    private RequiemPerShuggayListener.ReckoningListener reckoningListener;

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
            return "RequiemPerShuggayListener - InitActionButton";
        }
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, initActionButtonListener, enableCardListener, reckoningListener);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.RequiemPerShuggayAction> {

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
            name = "Requiem\n" +
                    "per Shuggay";
        }

        @Override
        protected String getAdditionalInfo() {
            if (isDisabled()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            List<MonsterInfo> monstersAtGates = getMonstersAtGates();
            for (MonsterInfo monsterAtLocation : monstersAtGates) {
                sb
                        .append(monsterAtLocation.getName())
                        .append(" (")
                        .append(monsterAtLocation.getCurrentHealth())
                        .append(")")
                        .append("\n");
            }
            return sb.toString();
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore -1. If you pass,\n" +
                    "you may spend 1 Sanity\n" +
                    "to discard 1 Monster\n" +
                    "on a space containing a Gate.\n";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/REQUIEM_PER_SHUGGAY.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
        return 1.0f;
    }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected RequiemPerShuggayAction createAction() {
            return new RequiemPerShuggayAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            boolean noMonsters = getMonstersAtGates().isEmpty();
            if (!enoughSanity) {
                disabledReason = "You do not have enough Sanity.";
                return true;
            }
            if (noMonsters) {
                disabledReason = "No Monsters at Gates";
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

        protected class RequiemPerShuggayAction extends AbstractActionPhaseAction {

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
                showCardRequest.setTitle("Test Lore to discard Monster.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    testLore();
                });
            }

            private void testLore() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                ServicePlatform.get().getTestService().test(Stat.LORE, -1, JUST_ONE).subscribe(this::afterTest);
                ServicePlatform.get().getService().release();
            }

            private void afterTest(TestData testData) {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }

                Question<Boolean> question = new Question<>();
                question.setTitle("Spend 1 Sanity to discard 1 Monster?");
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
                selectMonsterToDiscard();
                ServicePlatform.get().getService().release();
            }

            private void selectMonsterToDiscard() {
                List<MonsterInfo> monsters = getMonstersAtGates();

                SelectMonsterData selectMonsterData = new SelectMonsterData();
                selectMonsterData.setTitleText("Select Monster");
                selectMonsterData.setHideText("Display Monsters?");
                selectMonsterData.setAvailableMonsters(monsters);

                ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
            }

            private void onMonsterSelect(MonsterInfo monsterInfo) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
                if (ServicePlatform.get().getPerformedActions().canPerformAction(investigatorId)) {
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                }
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }

        }

        private List<MonsterInfo> getMonstersAtGates() {
            GateStackRead gateStackRead = ServicePlatform.get().getGateStackRead();
            List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
            monsters = Stream.collectToList(monsters, monsterInfo ->
                    gateStackRead.isGateAtLocation(monsterInfo.getCurrentLocation()) &&
                    !monsterInfo.isEpic());
            return monsters;
        }
    }

    private class ReckoningListener extends AbstractCardReckoningListener {

        @Override
        protected InvestigatorId getCardOwnerId() {
            return investigatorId;
        }

        @Override
        protected void onNotify() {
            ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getArtifactInfo())
                    .withTitle("Roll 1 die. On a 1 or 2, Doom advances.")
                    .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                    .build();
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onConfirm());
        }

        private void onConfirm() {
            RollData rollData = new RollData();
            rollData.setMinSuccessful(3);
            rollData.setMaxFailed(2);
            Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
            singleDieResult.subscribe(this::resolveDiceRoll);
        }

        private void resolveDiceRoll(Integer rolledValue) {
            if (rolledValue <= 2) {
                ServicePlatform.get().getDoomOmenService().advanceDoom();
            }
        }
    }

}
