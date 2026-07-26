package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.TtkaHalotArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.CatBurglarListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class TtkaHalotListener extends AbstractArtifactListener<TtkaHalotArtifact> {

    private static final Logger logger = LogManager.getLogger(CatBurglarListener.class);
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
            return "TtkaHalotListener - InitActionButton";
        }
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, initActionButtonListener, enableCardListener);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.TtkaHalotAction> {

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
            name = "T'tka\n" +
                    "Halot";
        }

        @Override
        protected String getAdditionalInfo() {
            if (isDisabled()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            List<MonsterInfo> monstersAtGates = getMonstersAtThisLocation();
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
                    "to choose 1 Monster\n" +
                    "on your space.\n" +
                    "The chosen Monster\n" +
                    "loses 3 Health.";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/TTKA_HALOT.jpg";
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
        protected TtkaHalotAction createAction() {
            return new TtkaHalotAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            boolean noMonsters = getMonstersAtThisLocation().isEmpty();
            if (!enoughSanity) {
                disabledReason = "You do not have enough Sanity.";
                return true;
            }
            if (noMonsters) {
                disabledReason = "No Monsters here.";
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

        protected class TtkaHalotAction extends AbstractActionPhaseAction {

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
                showCardRequest.setTitle("Test Lore to damage Monster.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    testLore();
                });
            }

            private void testLore() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                ServicePlatform.get().getTestService().test(Stat.LORE, 0, JUST_ONE).subscribe(this::afterTest);
                ServicePlatform.get().getService().release();
            }

            private void afterTest(TestData testData) {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }

                Question<Boolean> question = new Question<>();
                question.setTitle("Spend 1 Sanity to damage Monster?");
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
                damageMonster();
                ServicePlatform.get().getService().release();
            }

            private void damageMonster() {
                List<MonsterInfo> monsters = getMonstersAtThisLocation();

                SelectMonsterData selectMonsterData = new SelectMonsterData();
                selectMonsterData.setTitleText("Select Monster");
                selectMonsterData.setHideText("Display Monsters?");
                selectMonsterData.setAvailableMonsters(monsters);

                ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
            }

            private void onMonsterSelect(MonsterInfo monsterInfo) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 3);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }
        }

        private List<MonsterInfo> getMonstersAtThisLocation() {
            return ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getActiveInvestigator().getCurrentLocationId());
        }
    }
}
