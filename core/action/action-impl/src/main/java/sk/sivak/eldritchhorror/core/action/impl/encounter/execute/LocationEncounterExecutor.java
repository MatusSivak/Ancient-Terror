package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.model.LocationEncounterDeckWrite;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class LocationEncounterExecutor {

    private static final Logger logger = LogManager.getLogger(LocationEncounterExecutor.class);

    public static void execute(LocationEncounter input) {
        LocationEncounterDeckWrite locationEncounterDeck = ServicePlatform.get().getLocationEncounterDeck();
        Integer page = locationEncounterDeck.drawCard(input.getLocationEncounterType().getLocationName());
        logger.info("Drawing " + input.getLocationEncounterType().getLocationName() + " page=" + page);
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Location", input.getLocationEncounterType().getLocationName());
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideSelectEncounterTable();
        ServicePlatform.get().getService().showLocationBackground(LocationId.valueOf(input.getLocationEncounterType().name()));
        ServicePlatform.get().getEventListenerProvider().executeLocationEncounter(page, input.getLocationEncounterType());
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();
    }
}
