package sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AncientOneInitListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.LocationMapRead;
import sk.sivak.eldritchhorror.core.model.MythosDeckRead;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class CthulhuInitListener extends AncientOneInitListener {

    private static final Logger logger = LogManager.getLogger(CthulhuInitListener.class);
    private ReckoningListener reckoningListener;
    private AfterMoveListener afterMoveListener;
    private AfterAdvanceDoomListener afterAdvanceDoomListener;

    @Override
    public void register() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getGameService().showAncientOneCard();
        ServicePlatform.get().getMythosDeck().initSelectedMythosCards(
                new MythosDeckRead.MythosStageImpl(0,2,2),
                new MythosDeckRead.MythosStageImpl(1,3,0),
                new MythosDeckRead.MythosStageImpl(3,4,0));
        ServicePlatform.get().getService().release();

        reckoningListener = new ReckoningListener();
        afterMoveListener = new AfterMoveListener();
        afterAdvanceDoomListener = new AfterAdvanceDoomListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterMoveListener, BeforeAfterEvent.TRAVEL_TO_LOCATION);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterMoveListener, BeforeAfterEvent.SPAWN_INVESTIGATOR);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
    }

    @Override
    public void load() {
        reckoningListener = new ReckoningListener();
        afterMoveListener = new AfterMoveListener();
        afterAdvanceDoomListener = new AfterAdvanceDoomListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterMoveListener, BeforeAfterEvent.TRAVEL_TO_LOCATION);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterMoveListener, BeforeAfterEvent.SPAWN_INVESTIGATOR);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
    }

    private class AfterAdvanceDoomListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            if (ServicePlatform.get().getDoomTrack().getCurrentDoom() > 0) {
                return;
            }
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Cthulhu awakens! Investigators can't be replaced.");
            question.setPortraitBeforeTitle(AncientOneId.CTHULHU);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();

                ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
                ServicePlatform.get().getEventQueue().unregisterListener(afterMoveListener);
                ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);

                ServicePlatform.get().getDoomOmenService().ancientOneAwakens();

                ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }
    private class ReckoningListener extends EventListenerImpl<ReckoningFireType> {

        @Override
        public void onNotify(ReckoningFireType eventData) {
            if (eventData == ReckoningFireType.CHARGE) {
                return;
            }
            List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            if (investigators.isEmpty()) {
                return;
            }
            List<? extends InvestigatorRead> investigatorsOnASea = Stream.collectToList(investigators, investigator -> {
                LocationMapRead locationMap = ServicePlatform.get().getLocationMap();
                return locationMap.getLocationInfo(investigator.getCurrentLocationId()).getLocationType() == LocationType.SEA;
            });
            if (investigatorsOnASea.isEmpty()) {
                return;
            }
            List<? extends InvestigatorRead> withoutVortexes = Stream.collectToList(investigatorsOnASea, investigator ->
                    !ServicePlatform.get().getVortexes().isAtLocation(investigator.getCurrentLocationId()));

            if (withoutVortexes.isEmpty()) {
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Vortex is spawned under each investigator on a Sea.");
            question.setPortraitBeforeTitle(AncientOneId.CTHULHU);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                for (InvestigatorRead withoutVortex : withoutVortexes) {
                    ServicePlatform.get().getGameService().spawnVortex(withoutVortex.getCurrentLocationId());
                }
            });
            ServicePlatform.get().getService().release();

        }

        @Override
        public Class<ReckoningFireType> getDataClass() {
            return ReckoningFireType.class;
        }
    }

    private class AfterMoveListener extends EventListenerImpl<InvestigatorId> {

        @Override
        public void onNotify(InvestigatorId eventData) {
            if (eventData == null) {
                return;
            }
            InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getInvestigator(eventData);
            if (activeInvestigator.getCurrentLocationId() == null) {
                return;
            }
            List<LocationId> spawnedVortexes = ServicePlatform.get().getVortexes().getSpawnedVortexes();
            if (!spawnedVortexes.contains(activeInvestigator.getCurrentLocationId())) {
                return;
            }
            Question<Object> confirmation = new Question<>();
            confirmation.setPortraitBeforeTitle(AncientOneId.CTHULHU);
            confirmation.setTitle("The " + getActiveInvestigatorId().toString() + " becomes Delayed and loses one Sanity.");
            confirmation.setOptions(Question.Option.okOption);
            ServicePlatform.get().getGameService().ask(confirmation).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                DelayedData delayedData = new DelayedData(getActiveInvestigatorId(), true);
                ServicePlatform.get().getInvestigatorService().becomeDelayed(delayedData);
                ServicePlatform.get().getTokenService().loseSanity(1);
                ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<InvestigatorId> getDataClass() {
            return InvestigatorId.class;
        }
    }
}
