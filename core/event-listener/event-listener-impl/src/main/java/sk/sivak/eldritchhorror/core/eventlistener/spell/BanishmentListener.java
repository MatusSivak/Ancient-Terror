package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.banishment.BanishmentSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class BanishmentListener extends AbstractSpellListener<BanishmentSpell> {


    private BanishmentActionListener banishmentActionListener;
    private EnableCardListener enableCardListener;

    public BanishmentListener() {

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(banishmentActionListener, enableCardListener);
    }

    @Override
    protected void register() {
        banishmentActionListener = new BanishmentActionListener();
        getEventQueue().addBeforeEventListener(banishmentActionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class BanishmentActionListener extends AbstractActionPhaseListener {

        public BanishmentActionListener() {
            name = "Banishment";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore+2. If you pass,\n" +
                    "discard one Monster\n" +
                    "on the nearest Gate\n" +
                    "with toughness\n" +
                    "equal to or less than\n" +
                    "your test result.\n\n" +
                    "Then flip this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/spell/BANISHMENT.jpg";
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
        protected BanishmentAction createAction() {
            return new BanishmentAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == spellOwnerId;
        }

        @Override
        protected boolean isDisabled() {
            if (!gateExistsWithNonEpicMonster()) {
                disabledReason = "No Monster on a Gate"; // THIS MUST BE LAST
                return true;
            }
            if (!enabled) {
                disabledReason = "Action already performed this round"; // THIS MUST BE LAST
                return true;
            }
            return false;
        }

        private boolean gateExistsWithNonEpicMonster() {
            for (GateInfo spawnedGate : ServicePlatform.get().getGateStackRead().getSpawnedGates()) {
                if (!ServicePlatform.get().getMonsterCup().containsMonster(spawnedGate.getLocationId())) {
                    continue;
                }
                boolean containsRegularMonster = Stream.anyMatch(ServicePlatform.get().getMonsterCup().getMonstersAtLocation(spawnedGate.getLocationId()),
                        monsterInfo -> !monsterInfo.isEpic());
                if (containsRegularMonster) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }
    }

    private class BanishmentAction extends AbstractActionPhaseAction implements CardInfoAware<BanishmentSpell> {


        @Override
        public void execute() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
            showCardRequest.setTitle("Cast Banishment.");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, 2, 16,
                        new TestFlavorRequest(TestFlavorType.SPELL));
                test.subscribe(this::withTestResult);
            });
        }

        private void withTestResult(TestData testResult) {

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setMainSpellAction(new MainSpellAction(testResult.getScore(), spellBackListener));
            spellBackListener.executeWhole();
            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private class MainSpellAction implements Runnable {
            private final int testResultScore;
            private final AbstractSpellBackListener spellBackListener;

            MainSpellAction(int testResultScore, AbstractSpellBackListener spellBackListener) {
                this.testResultScore = testResultScore;
                this.spellBackListener = spellBackListener;
            }

            @Override
            public void run() {
                List<GateInfo> nearestGates = findNearestGates();
                List<MonsterInfo> filteredMonsters = new LinkedList<>();
                for (GateInfo nearestGate : nearestGates) {
                    List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(nearestGate.getLocationId());
                    filteredMonsters.addAll(Stream.collectToList(monstersAtLocation,
                            monsterInfo -> !monsterInfo.isEpic() && monsterInfo.getToughness() <= testResultScore));
                }
                if (filteredMonsters.isEmpty()) {
                    Question<Object> question = new Question<>();
                    question.setOptions(Question.Option.okOption);
                    question.setTitle("No Monster can be discarded.");
                    ServicePlatform.get().getGameService().ask(question).subscribe();
                    return;
                }
                SelectMonsterData selectMonsterData = new SelectMonsterData();
                selectMonsterData.setAvailableMonsters(filteredMonsters);
                ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(monsterInfo -> {
                    spellBackListener.setData("locationId",monsterInfo.getCurrentLocation());
                    spellBackListener.setData("monsterInfo",monsterInfo);
                    ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
                });
            }
        }

        @Override
        public BanishmentSpell getCardInfo() {
            return spellInfo;
        }
    }

    public static List<GateInfo> findNearestGates() {
        List<GateInfo> gatesWithMonsters = new LinkedList<>();
        for (GateInfo spawnedGate : ServicePlatform.get().getGateStackRead().getSpawnedGates()) {
            if (!ServicePlatform.get().getMonsterCup().containsMonster(spawnedGate.getLocationId())) {
                continue;
            }
            boolean containsRegularMonster = Stream.anyMatch(ServicePlatform.get().getMonsterCup().getMonstersAtLocation(spawnedGate.getLocationId()),
                    monsterInfo -> !monsterInfo.isEpic());
            if (containsRegularMonster) {
                gatesWithMonsters.add(spawnedGate);
            }
        }

        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        TreeMap<Integer, List<GateInfo>> gatesMap = new TreeMap<>();

        for (GateInfo gateWithMonster : gatesWithMonsters) {
            FindNearestData nearest = ServicePlatform.get().getLocationMap().findNearest(gateWithMonster.getLocationId(),
                    locationInfo -> locationInfo.getLocationId().equals(currentLocationId));
            if (gatesMap.get(nearest.getDistance()) == null) {
                gatesMap.put(nearest.getDistance(), new LinkedList<>());
            }
            gatesMap.get(nearest.getDistance()).add(gateWithMonster);
        }

        return gatesMap.firstEntry().getValue();
    }


}
