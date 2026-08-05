package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
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

import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_7;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class BlueMythosCard2 implements MythosCardEventListener{

    private static final String RUMOR_CARD_ID = "StarsAligned";
    private ReckoningListener reckoningListener;
    private CollectCommonEncountersListener collectCommonEncountersListener;
    private EncounterOngoingRumorListener encounterOngoingRumorListener;

    @Override
    public void execute() {

        justLoad();

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getDoomOmenService().spawnStorm(RUMOR_CARD_ID, SPACE_7);

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
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
                ServicePlatform.get().getDoomOmenService().highlightRumorReckoning(RUMOR_CARD_ID);
                ServicePlatform.get().getDoomOmenService().advanceOmen();
                ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
                ServicePlatform.get().getService().convertTo(Object.class, () -> in);
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
            if (!eventData.getSecondLine().equals("rumor.card.starsAligned.title")) {
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("Find these strangers based on observation of the stars (Test Observation).");
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

                    Question<Object> question = new Question<>();
                    question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
                    question.setTitle("You convince la policia to remove these strangers.");
                    ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getDoomOmenService().highlightRumorObjective(RUMOR_CARD_ID);
                        unregister();
                        ServicePlatform.get().getService().convertTo(RumorEncounter.class, () -> eventData);
                        ServicePlatform.get().getService().release();
                    });
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
