package sk.sivak.eldritchhorror.core.eventlistener.spell;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
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
import sk.sivak.eldritchhorror.core.constants.spell.arcaneinsight.ArcaneInsightSpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ArcaneInsightListener extends AbstractSpellListener<ArcaneInsightSpell> {


    private ArcaneInsightActionListener arcaneInsightActionListener;
    private EnableCardListener enableCardListener;

    public ArcaneInsightListener() {

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(arcaneInsightActionListener, enableCardListener);
    }

    @Override
    protected void register() {
        arcaneInsightActionListener = new ArcaneInsightActionListener();
        getEventQueue().addBeforeEventListener(arcaneInsightActionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class ArcaneInsightActionListener extends AbstractActionPhaseListener {

        public ArcaneInsightActionListener() {
            name = "Arcane\nInsight";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Choose yourself or another investigator on any space and test Lore-2.\n" +
                    "Roll one additional die\n" +
                    "for each Tome possession\n" +
                    "you have. If you pass,\n" +
                    "that investigator\n" +
                    "gains one Clue.\n" +
                    "Then flip this card.";
        }

        @Override
        protected String getTexturePath() {
            return "card/spell/ARCANE_INSIGHT.jpg";
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
        protected ArcaneInsightAction createAction() {
            return new ArcaneInsightAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == spellOwnerId;
        }

        @Override
        protected boolean isDisabled() {
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

    private class ArcaneInsightAction extends AbstractActionPhaseAction implements CardInfoAware<ArcaneInsightSpell> {


        @Override
        public void execute() {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(spellInfo);
            showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
            showCardRequest.setTitle("Cast Arcane Insight.");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);
            displaySpell.subscribe(response -> {
                InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
                investigatorRestriction.setTitle("Select target of this Spell");
                investigatorRestriction.addAllowedInvestigators(Stream.map(ServicePlatform.get().getInvestigators().getOnBoardInvestigators(),
                        investigator -> investigator.getInfo().getInvestigatorId()));
                ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction).subscribe(this::afterInvestigatorSelect);
            });
        }

        private void afterInvestigatorSelect(InvestigatorId investigatorId) {
            List<? extends CardInfo> tomePossessions = getTomePossessions();
            TestFlavorRequest flavor = new TestFlavorRequest(TestFlavorType.SPELL);
            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, -2 + tomePossessions.size(), 3, flavor);
            if (tomePossessions.isEmpty()) {
                test.subscribe(testData -> withTestResult(testData, investigatorId));
            } else {
                Question<Object> question = new Question<>();
                if (tomePossessions.size() == 1) {
                    question.setTitle("You have "+tomePossessions.size()+" Tome.");
                } else {
                    question.setTitle("You have "+tomePossessions.size()+" Tomes.");
                }

                Question.SelectComponentsTableData<CardInfo> selectComponentsTableData = new Question.SelectComponentsTableData<>();
                selectComponentsTableData.setType(Question.SelectComponentsTableType.CARDS);
                selectComponentsTableData.setAvailableKeys(tomePossessions);
                question.setSelectComponentsTableData(selectComponentsTableData);
                question.setOptions(Question.Option.okOption);

                ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                    test.subscribe(testData -> withTestResult(testData, investigatorId));
                });
            }
        }

        private void withTestResult(TestData testResult, InvestigatorId investigatorId) {

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            AbstractSpellBackListener spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testResult);
            spellBackListener.setMainSpellAction(new MainSpellAction(spellBackListener, investigatorId));
            spellBackListener.executeWhole();
            ServicePlatform.get().getInvestigatorService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private class MainSpellAction implements Runnable {
            private final AbstractSpellBackListener spellBackListener;
            private final InvestigatorId investigatorId;

            private MainSpellAction(AbstractSpellBackListener spellBackListener, InvestigatorId investigatorId) {
                this.spellBackListener = spellBackListener;
                this.investigatorId = investigatorId;
            }

            @Override
            public void run() {
                spellBackListener.setData("investigatorId", investigatorId);
                InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
                if (activeInvestigatorId == investigatorId) {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                } else {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    ServicePlatform.get().getService().release();
                }
            }
        }

        @Override
        public ArcaneInsightSpell getCardInfo() {
            return spellInfo;
        }
    }

    private List<? extends CardInfo> getTomePossessions() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.TOME);
    }

}
