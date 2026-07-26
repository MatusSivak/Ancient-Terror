package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.util.IterableUtils;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.poisonmist.PoisonMistSpell;
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

public class PoisonMistListener extends AbstractSpellListener<PoisonMistSpell> {


    private PoisonMistActionListener poisonMistActionListener;
    private EnableCardListener enableCardListener;

    public PoisonMistListener() {

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(poisonMistActionListener, enableCardListener);
    }

    @Override
    protected void register() {
        poisonMistActionListener = new PoisonMistActionListener();
        getEventQueue().addBeforeEventListener(poisonMistActionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class PoisonMistActionListener extends AbstractActionPhaseListener {

        public PoisonMistActionListener() {
            name = "Poison\nMist";
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
                        .append(monsterAtLocation.getToughness())
                        .append(")")
                        .append("\n");
            }
            return sb.toString();
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore +1. If you pass,\n" +
                    "discard Monsters from your space\n" +
                    "with total toughness\n" +
                    "equal to or less than\n" +
                    "your test result.\n" +
                    "Then flip this card";
        }

        @Override
        protected String getTexturePath() {
            return "card/spell/POISON_MIST.jpg";
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
        protected PoisonMistAction createAction() {
            return new PoisonMistAction();
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

    private class PoisonMistAction extends AbstractActionPhaseAction implements CardInfoAware<PoisonMistSpell> {

        private AbstractSpellBackListener spellBackListener;
        private TestData testResult;

        @Override
        public void execute() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
            showCardRequest.setTitle("Cast Poison Mist");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, +1, 16,
                        new TestFlavorRequest(TestFlavorType.SPELL));
                test.subscribe(this::disableAndFlipCard);
            });
        }


        private void disableAndFlipCard(TestData testResult) {
            this.testResult = testResult;
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setMainSpellAction(createMainSpellAction());
            spellBackListener.executeWhole();

            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private int score;
        private Runnable createMainSpellAction() {
            return () -> {
                score = testResult.getScore();
                ServicePlatform.get().getService().hold();
                LocationId currentLocationId = ServicePlatform.get().getInvestigators().getInvestigator(spellOwnerId).getCurrentLocationId();
                List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);

                IterableUtils.removeIf(monstersAtLocation, MonsterInfo::isEpic);

                discardAndRepeat(monstersAtLocation);

                ServicePlatform.get().getService().release();
            };
        }

        private void discardAndRepeat(List<MonsterInfo> monstersAtLocation) {
            IterableUtils.removeIf(monstersAtLocation, monsterInfo -> monsterInfo.getToughness() > score);
            if (monstersAtLocation.isEmpty()) {
                return;
            }
            SelectMonsterData selectMonsterData = new SelectMonsterData();
            selectMonsterData.setAvailableMonsters(monstersAtLocation);
            selectMonsterData.setTitleText("Select Monster ( Toughness remaining: " + score +" )");
            ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(monsterInfo -> {
                if (monsterInfo == null) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
                score -= monsterInfo.getToughness();
                monstersAtLocation.remove(monsterInfo);
                discardAndRepeat(monstersAtLocation);
                ServicePlatform.get().getService().release();
            });
        }


        @Override
        public PoisonMistSpell getCardInfo() {
            return spellInfo;
        }
    }


}
