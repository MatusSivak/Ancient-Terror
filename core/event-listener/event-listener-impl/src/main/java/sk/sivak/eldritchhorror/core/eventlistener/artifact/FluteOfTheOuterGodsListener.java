package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.FluteOfTheOuterGodsArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
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

public class FluteOfTheOuterGodsListener extends AbstractArtifactListener<FluteOfTheOuterGodsArtifact> {

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
            return "FluteOfTheOuterGodsListener - InitActionButton";
        }
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, initActionButtonListener, enableCardListener);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.FluteOfTheOuterGodsAction> {

        private boolean enoughSanityAndHealth;

        private void init() {
            if (!isVisible()) {
                return;
            }
            ServicePlatform.get().getTokenService().canSpend(0,0,2,2).subscribe(spendData -> {
                enoughSanityAndHealth = spendData.hasEnough();
            });
        }

        public ActionListener() {
            name = "Flute of the\n" +
                    "Outer Gods";
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
            return "Spend 2 Health\n" +
                    "and 2 Sanity\n" +
                    "to defeat all Monsters\n" +
                    "on your space.";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/FLUTE_OF_THE_OUTER_GODS.jpg";
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
        protected FluteOfTheOuterGodsAction createAction() {
            return new FluteOfTheOuterGodsAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            boolean noMonsters = getMonstersAtThisLocation().isEmpty();
            if (!enoughSanityAndHealth) {
                disabledReason = "You do not have enough Sanity and Health.";
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

        protected class FluteOfTheOuterGodsAction extends AbstractActionPhaseAction {

            @Override
            public void execute() {
                ServicePlatform.get().getTokenService().canSpend(0,0,2,2).subscribe(this::onCanSpend);
            }

            private void onCanSpend(SpendData spendData) {
                if (!spendData.hasEnough()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ON_YES);
                showCardRequest.setTitle("Spend 2 Sanity and 2 Health to defeat all Monsters?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(this::onAnswer);
            }

            private void onAnswer(ShowCardResponse response) {
                if (response == ShowCardResponse.YES) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().disableCard(getArtifactInfo());
                    ServicePlatform.get().getTokenService().spend(0,0,2,2).subscribe(this::afterSpendAction);
                    ServicePlatform.get().getService().release();
                } else {
                    addFreeAction();
                }
            }

            private void afterSpendAction(SpendData spendData) {
                if (spendData.hasEnough()) {
                    ServicePlatform.get().getService().hold();
                    spendData.pay();
                    for (MonsterInfo monsterInfo : getMonstersAtThisLocation()) {
                        ServicePlatform.get().getMonsterService().defeatMonster(monsterInfo, false, false);
                    }
                    ServicePlatform.get().getService().convertToNull();
                    ServicePlatform.get().getService().release();
                } else {
                    addFreeAction();
                }
            }

            private void addFreeAction() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getBasicActionService().addFreeAction();
                ServicePlatform.get().getBasicActionService().removePerformedAction(this);
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }
        }

        private List<MonsterInfo> getMonstersAtThisLocation() {
            LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
            List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
            monsters = Stream.collectToList(monsters, monsterInfo ->
                            monsterInfo.getCurrentLocation() == currentLocationId &&
                                    !monsterInfo.isEpic());
            return monsters;
        }
    }
}
