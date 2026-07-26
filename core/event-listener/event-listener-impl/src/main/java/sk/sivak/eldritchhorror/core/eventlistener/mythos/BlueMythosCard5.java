package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.RumorEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.rumor.InitRumorCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_7;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_8;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class BlueMythosCard5 implements MythosCardEventListener{

    private static final String RUMOR_CARD_ID = "GrowingMadness";
    private ReckoningListener reckoningListener;
    private CollectCommonEncountersListener collectCommonEncountersListener;
    private EncounterOngoingRumorListener encounterOngoingRumorListener;

    @Override
    public void execute() {

        justLoad();

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getDoomOmenService().spawnStorm(RUMOR_CARD_ID, SPACE_8);

        InitRumorCardData initRumorCardData = new InitRumorCardData();
        initRumorCardData.setRumorId(RUMOR_CARD_ID);
        initRumorCardData.setCluesRequired(Math.round(ServicePlatform.get().getModel().getReferenceCard().getPlayers() / 2f));
        ServicePlatform.get().getDoomOmenService().initRumorCard(initRumorCardData);

        ServicePlatform.get().getDoomOmenService().showRumorCard(RUMOR_CARD_ID);
        ServicePlatform.get().getService().release();

    }

    private class ReckoningListener extends EventListenerImpl<ReckoningFireType> {

        @Override
        public void onNotify(ReckoningFireType eventData) {
            if (eventData == ReckoningFireType.CHARGE) {
                return;
            }
            InvestigatorRead leadInvestigator = ServicePlatform.get().getInvestigators().getLeadInvestigator();
            if (leadInvestigator == null) {
                return;
            }
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
                ServicePlatform.get().getDoomOmenService().highlightRumorReckoning(RUMOR_CARD_ID);
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(leadInvestigator.getInfo().getInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                ServicePlatform.get().getGameService().gainCondition(ConditionTrait.MADNESS);
                countdownForEachMadness(leadInvestigator.getInfo().getInvestigatorId());
                ServicePlatform.get().getService().release();
            });
        }

        private void countdownForEachMadness(InvestigatorId investigatorId) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                List<ConditionInfo> madnessConditions = ServicePlatform.get().getConditionsDeck().getCondition(investigatorId, ConditionTrait.MADNESS);

                ServicePlatform.get().getService().hold();

                if (madnessConditions.size() > 1) {
                    Question<Object> question = new Question<>();
                    Question.SelectComponentsTableData<ConditionInfo> selectComponentsTableData = new Question.SelectComponentsTableData<>();
                    selectComponentsTableData.setType(Question.SelectComponentsTableType.CARDS);
                    selectComponentsTableData.setAvailableKeys(madnessConditions);
                    question.setSelectComponentsTableData(selectComponentsTableData);
                    question.setTitle("Owned Madness Conditions.");
                    question.setOptions(Question.Option.okOption);
                    ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                        confirmOwnedMadnesses(in, madnessConditions);
                    });
                } else {
                    confirmOwnedMadnesses(in, madnessConditions);
                }

                ServicePlatform.get().getService().release();
            });
        }

        private void confirmOwnedMadnesses(Object in, List<ConditionInfo> madnessConditions) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().countdownRumorCard(RUMOR_CARD_ID, madnessConditions.size());
            if (ServicePlatform.get().getRumors().getActiveRumor(RUMOR_CARD_ID).getTimeRemaining() - madnessConditions.size() <= 0) {
                ServicePlatform.get().getDoomOmenService().highlightRumorFailure(RUMOR_CARD_ID);
                triggerFailureEffect();
                unregister();
                ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
            }
            ServicePlatform.get().getService().convertTo(Object.class, () -> in);
            ServicePlatform.get().getService().release();
        }

        private void triggerFailureEffect() {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                    ServicePlatform.get().getTokenService().loseSanity(3);
                }
                ServicePlatform.get().getService().convertTo(Object.class, ()-> in);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<ReckoningFireType> getDataClass() {
            return ReckoningFireType.class;
        }
    }

    private class CollectCommonEncountersListener extends EventListenerImpl<AvailableEncounters> {

        @Override
        public void onNotify(AvailableEncounters eventData) {
            InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
            RumorCardInfo activeRumor = ServicePlatform.get().getRumors().getActiveRumor(RUMOR_CARD_ID);
            if (!activeRumor.getRumorLocation().equals(activeInvestigator.getCurrentLocationId())) {
                return;
            }
            ServicePlatform.get().getTokenService().canSpend(activeRumor.getCluesRequired(),0,0,0)
                    .subscribe(spendData -> onCanSpend(spendData,activeRumor, eventData));
        }

        private void onCanSpend(SpendData spendData, RumorCardInfo activeRumor, AvailableEncounters eventData) {
            RumorEncounter encounter = new RumorEncounter(activeRumor.getTitleText());
            eventData.addEncounter(encounter);
            if (!spendData.hasEnough()) {
                encounter.disable("Not enough Clues");
            }
            ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> eventData);
        }


        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }

    private class EncounterOngoingRumorListener extends EventListenerImpl<RumorEncounter> {

        @Override
        public void onNotify(RumorEncounter eventData) {
            if (!eventData.getSecondLine().equals("Growing Madness")) {
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("Find the uncharted isle (Test Observation).");
            question.displayOngoingRumorCard(RUMOR_CARD_ID);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> onConfirm(eventData));
        }

        private void onConfirm(RumorEncounter eventData) {
            ServicePlatform.get().getTestService().test(Stat.OBSERVATION, 0, JUST_ONE).subscribe(testData -> {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getService().convertTo(RumorEncounter.class, () -> eventData);
                    return;
                }
                onSuccessfulTest(eventData);
            });
        }

        private void onSuccessfulTest(RumorEncounter eventData) {
            RumorCardInfo activeRumor = ServicePlatform.get().getRumors().getActiveRumor(RUMOR_CARD_ID);
            ServicePlatform.get().getTokenService().canSpend(activeRumor.getCluesRequired(), 0,0,0).subscribe(canSpend -> {
                if (!canSpend.hasEnough()) {
                    confirmNotEnoughClues(eventData);
                    return;
                }
                Question<Boolean> spendClueQuestion = new Question<>();
                spendClueQuestion.setOptions(Question.Option.noYesOptions);
                if (activeRumor.getCluesRequired() == 1) {
                    spendClueQuestion.setTitle("Spend one Clue to solve this Rumor?");
                } else {
                    spendClueQuestion.setTitle("Spend "+activeRumor.getCluesRequired()+" Clues to solve this Rumor?");
                }

                ServicePlatform.get().getGameService().ask(spendClueQuestion).subscribe(noYes -> {
                    if (!noYes.getResponseData()) {
                        ServicePlatform.get().getService().convertTo(RumorEncounter.class, () -> eventData);
                    } else {
                        onSpendClueAnswer(activeRumor, eventData);
                    }
                });
            });
        }

        private void onSpendClueAnswer(RumorCardInfo activeRumor, RumorEncounter eventData) {
            ServicePlatform.get().getTokenService().spend(activeRumor.getCluesRequired(), 0, 0, 0).subscribe(spendData -> {
                if (!spendData.hasEnough()) {
                    confirmNotEnoughClues(eventData);
                } else {
                    ServicePlatform.get().getService().hold();
                    spendData.pay();
                    ServicePlatform.get().getDoomOmenService().highlightRumorObjective(RUMOR_CARD_ID);
                    unregister();
                    ServicePlatform.get().getService().convertTo(RumorEncounter.class, () -> eventData);
                    ServicePlatform.get().getService().release();
                }
            });
        }

        private void confirmNotEnoughClues(RumorEncounter eventData) {
            Question<Object> question2 = new Question<>();
            question2.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question2.setTitle("Not enough Clues to solve this Rumor.");
            ServicePlatform.get().getGameService().ask(question2).subscribe(answer2 -> {
                ServicePlatform.get().getService().convertTo(RumorEncounter.class, () -> eventData);
            });
        }

        @Override
        public Class<RumorEncounter> getDataClass() {
            return RumorEncounter.class;
        }
    }

    public void justLoad() {
        reckoningListener = new ReckoningListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_RUMORS);
        collectCommonEncountersListener = new CollectCommonEncountersListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(collectCommonEncountersListener, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        encounterOngoingRumorListener = new EncounterOngoingRumorListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(encounterOngoingRumorListener, DirectEvent.ENCOUNTER_ONGOING_RUMOR);
    }

    private void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
        ServicePlatform.get().getEventQueue().unregisterListener(collectCommonEncountersListener);
        ServicePlatform.get().getEventQueue().unregisterListener(encounterOngoingRumorListener);
    }
}
