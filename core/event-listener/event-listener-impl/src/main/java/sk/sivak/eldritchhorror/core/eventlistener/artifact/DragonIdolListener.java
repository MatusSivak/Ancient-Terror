package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.DragonIdolArtifact;
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
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetEventListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.CatBurglarListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData.CountRerollDiceType.OTHER;

public class DragonIdolListener extends AbstractArtifactListener<DragonIdolArtifact> {

    private static final Logger logger = LogManager.getLogger(CatBurglarListener.class);
    private ActionListener actionListener;
    private EnableCardListener enableCardListener;
    private RerollUsingAssetsListener rerollUsingAssetsListener;

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);
        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_2);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, enableCardListener, rerollUsingAssetsListener);
    }


    private class ActionListener extends AbstractActionPhaseListener<ActionListener.DragonIdolAction> {


        public ActionListener() {
            name = "Dragon\n" +
                    "Idol";
        }

        @Override
        protected String getAdditionalInfo() {
            if (isDisabled()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            List<MonsterInfo> monstersAtGates = getMonstersNearby();
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
            return
                    "Lose 1 Sanity\n" +
                    "and 1 Monster\n" +
                    "on your space\n" +
                    "or an adjacent space\n" +
                    "loses 2 health.\n";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/DRAGON_IDOL.jpg";
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
        protected DragonIdolAction createAction() {
            return new DragonIdolAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            boolean noMonsters = getMonstersNearby().isEmpty();
            if (noMonsters) {
                disabledReason = "No Monsters nearby.";
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

        protected class DragonIdolAction extends AbstractActionPhaseAction {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Deal 2 Damage to Monster.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    List<MonsterInfo> monsters = getMonstersNearby();

                    SelectMonsterData selectMonsterData = new SelectMonsterData();
                    selectMonsterData.setTitleText("Select Monster");
                    selectMonsterData.setHideText("Display Monsters?");
                    selectMonsterData.setAvailableMonsters(monsters);

                    ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
                });
            }


            private void onMonsterSelect(MonsterInfo monsterInfo) {
                dealDamageToMonster(monsterInfo);
            }

            private void dealDamageToMonster(MonsterInfo monsterInfo) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                ServicePlatform.get().getTokenService().loseSanity(1);
                ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
                ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 2);
                if (ServicePlatform.get().getPerformedActions().canPerformAction(investigatorId)) {
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                }
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }
        }

        private List<MonsterInfo> getMonstersNearby() {
            List<LocationId> relevantLocations = new LinkedList<>();
            LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
            relevantLocations.add(currentLocationId);
            List<LocationInfo.Connection> connections = ServicePlatform.get().getLocationMap().getLocationInfo(currentLocationId).getConnections();
            for (LocationInfo.Connection connection : connections) {
                relevantLocations.add(connection.getLocationId());
            }

            List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
            monsters = Stream.collectToList(monsters, monsterInfo ->
                    relevantLocations.contains(monsterInfo.getCurrentLocation()));
            return monsters;
        }
    }



    private class RerollUsingAssetsListener extends AbstractArtifactEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(DragonIdolListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                ServicePlatform.get().getTokenService().canSpend(0, 0, 0, 1).subscribe(response -> onCanSpend(response, input));
            };
        }

        private void onCanSpend(SpendData spendData, TestData input) {
            if (!spendData.hasEnough()) {
                ServicePlatform.get().getGameService().convertTo(TestData.class, () -> input);
                return;
            }
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setArtifactInfo(getArtifactInfo());
            showCardRequest.setTitle("Reroll dice for Sanity?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

            ServicePlatform.get().getGameService().showCard(showCardRequest)
                    .subscribe(response -> onAnswer(response, input));

        }

        private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                onUseAxe(input);
            } else {
                ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
            }

        }

        private void onUseAxe(TestData input) {
            ServicePlatform.get().getTokenService().spend(0, 0, 0, 1).subscribe(response -> onSpend(response, input));
        }

        private void onSpend(SpendData spendData, TestData input) {
            if (spendData.hasEnough()) {
                int maxDice = input.getCalculatedDicePool() - input.getSuccessRolls();
                if (maxDice == 1) {
                    ServicePlatform.get().getService().hold();
                    spendData.pay();
                    ServicePlatform.get().getTestService().rerollDie(input);
                    ServicePlatform.get().getService().release();
                } else {
                    Single<Answer<Integer, Object>> answer = askHowManyDiceToReroll(maxDice);
                    answer.subscribe(amount -> {
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        ServicePlatform.get().getTestService().findCountRerollDice(amount.getResponseData(),
                                OTHER,
                                input);
                        ServicePlatform.get().getTestService().rerollDice();
                        ServicePlatform.get().getService().release();
                    }, logger::error);
                }
            } else {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                showCardRequest.setTitle("Not enough Sanity");
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> notEnoughSanity(showCardResponse, input));
            }
        }

        private void notEnoughSanity(ShowCardResponse showCardResponse, TestData input) {
            ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
        }

        private Single<Answer<Integer, Object>> askHowManyDiceToReroll(int maxDice) {
            Question<Integer> question = new Question<>();
            question.setTitle("How many dice you want to reroll?");
            question.setOptions(new LinkedList<>());
            for (int i = 1; i <= maxDice; i++) {
                question.getOptions().add(new Question.Option<>("" + i, i));
            }
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
