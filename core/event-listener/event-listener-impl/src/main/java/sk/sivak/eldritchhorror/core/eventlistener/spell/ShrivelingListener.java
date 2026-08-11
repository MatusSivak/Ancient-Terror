package sk.sivak.eldritchhorror.core.eventlistener.spell;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.shriveling.ShrivelingSpell;
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
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Arrays;
import java.util.List;

public class ShrivelingListener extends AbstractSpellListener<ShrivelingSpell> {


    private ShrivelingActionListener shrivelingActionListener;
    private EnableCardListener enableCardListener;

    public ShrivelingListener() {

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(shrivelingActionListener, enableCardListener);
    }

    @Override
    protected void register() {
        shrivelingActionListener = new ShrivelingActionListener();
        getEventQueue().addBeforeEventListener(shrivelingActionListener, BeforeAfterEvent.FIND_ACTIONS);
        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class ShrivelingActionListener extends AbstractActionPhaseListener {

        public ShrivelingActionListener() {
            name = "Shriveling";
        }

        @Override
        protected String getAdditionalInfo() {
            if (isDisabled()) {
                return null;
            }
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getActiveInvestigator().getCurrentLocationId());
            StringBuilder sb = new StringBuilder();
            for (MonsterInfo monsterAtLocation : monstersAtLocation) {
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
            return "Test Lore. If you pass, " +
                    "choose a Monster on your space " +
                    "to lose 2 Health.\n" +
                    "Then flip this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/spell/SHRIVELING.jpg";
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
        protected ShrivelingAction createAction() {
            return new ShrivelingAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == spellOwnerId;
        }

        @Override
        protected boolean isDisabled() {
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getActiveInvestigator().getCurrentLocationId());
            if (monstersAtLocation == null || monstersAtLocation.isEmpty()) {
                disabledReason = "There are no Monsters here.";
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
    }

    private class ShrivelingAction extends AbstractActionPhaseAction implements CardInfoAware<ShrivelingSpell> {

        private AbstractSpellBackListener spellBackListener;

        @Override
        public void execute() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
            showCardRequest.setTitle("Cast Shriveling");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, 0, 16,
                        new TestFlavorRequest(TestFlavorType.SPELL));
                test.subscribe(this::withTestResult);
            });
        }

        private void withTestResult(TestData testResult) {
            if (testResult.isSuccessful()) {
                selectSingleMonster(testResult);
            } else {
                disableAndFlipCard(testResult, null);
            }
        }

        private void disableAndFlipCard(TestData testData, MonsterInfo monsterInfo) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testData);
            spellBackListener.setData("monsterInfo",monsterInfo);
            spellBackListener.executeWhole();

            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private void selectSingleMonster(TestData testResult) {
            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getInvestigator(spellOwnerId).getCurrentLocationId();
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);
            SelectMonsterData selectMonsterData = new SelectMonsterData();
            selectMonsterData.setAvailableMonsters(monstersAtLocation);
            selectMonsterData.setHideText("Display Monsters?");
            selectMonsterData.setTitleText("Select Monster");
            ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData)
                    .subscribe(monsterInfo -> onMonsterSelect(monsterInfo, testResult));
        }

        private void onMonsterSelect(MonsterInfo monsterInfo, TestData testResult) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 2);
            disableAndFlipCard(testResult, monsterInfo);
            ServicePlatform.get().getService().release();
        }

        @Override
        public ShrivelingSpell getCardInfo() {
            return spellInfo;
        }
    }


}
