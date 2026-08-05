package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.OtherWorldEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.RumorEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.rumor.InitRumorCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;

import static sk.sivak.eldritchhorror.core.constants.location.LocationId.SPACE_2;

public class BlueMythosCard3 implements MythosCardEventListener{

    private static final String RUMOR_CARD_ID = "FracturedReality";
    private ReckoningListener reckoningListener;
    private CollectCommonEncountersListener collectCommonEncountersListener;
    private EncounterOngoingRumorListener encounterOngoingRumorListener;
    private BeforeCloseGateListener beforeCloseGateListener;

    @Override
    public void execute() {

        justLoad();

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getDoomOmenService().spawnStorm(RUMOR_CARD_ID, SPACE_2);

        InitRumorCardData initRumorCardData = new InitRumorCardData();
        initRumorCardData.setRumorId(RUMOR_CARD_ID);
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
                ServicePlatform.get().getDoomOmenService().countdownRumorCard(RUMOR_CARD_ID, 1);
                if (ServicePlatform.get().getRumors().getActiveRumor(RUMOR_CARD_ID).getTimeRemaining() == 1) {
                    ServicePlatform.get().getDoomOmenService().highlightRumorFailure(RUMOR_CARD_ID);
                    triggerFailureEffect();
                    unregister();
                    ServicePlatform.get().getGameService().checkIfSomeoneIsDefeated();
                }
                ServicePlatform.get().getService().convertTo(Object.class, () -> in);
                ServicePlatform.get().getService().release();
            });
        }

        private void triggerFailureEffect() {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                for (GateInfo spawnedGate : ServicePlatform.get().getGateStackRead().getSpawnedGates()) {
                    ServicePlatform.get().getService().moveCameraToLocation(spawnedGate.getLocationId());
                    ServicePlatform.get().getDoomOmenService().advanceDoom();
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
            RumorEncounter encounter = new RumorEncounter(activeRumor.getTitleText());
            eventData.addEncounter(encounter);
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
            if (!eventData.getSecondLine().equals("rumor.card.fracturedReality.title")) {
                return;
            }
            beforeCloseGateListener = new BeforeCloseGateListener();
            ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeCloseGateListener, BeforeAfterEvent.CLOSE_GATE);
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("Use an ancient portal.");
            question.displayOngoingRumorCard(RUMOR_CARD_ID);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> onConfirm(eventData));
        }

        private void onConfirm(RumorEncounter eventData) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().convertTo(Encounter.class, () -> {
                OtherWorldEncounter otherWorldEncounter = new OtherWorldEncounter(null, null);
                otherWorldEncounter.setHideEncounterTable(false);
                return otherWorldEncounter;
            });
            ServicePlatform.get().getEncounterService().executeEncounter();
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(beforeCloseGateListener);
            });
            ServicePlatform.get().getService().convertTo(RumorEncounter.class, () -> eventData);
            ServicePlatform.get().getService().release();
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

    private class BeforeCloseGateListener extends EventListenerImpl<Object>{

        @Override
        public void onNotify(Object eventData) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().highlightRumorObjective(RUMOR_CARD_ID);
            unregister();
            ServicePlatform.get().getService().convertTo(Object.class, () -> eventData);
            ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.CLOSE_GATE, eventData);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }
}
