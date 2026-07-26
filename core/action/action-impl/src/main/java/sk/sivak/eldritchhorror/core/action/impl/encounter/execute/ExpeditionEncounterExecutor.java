package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ExpeditionEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class ExpeditionEncounterExecutor {
    private static final Logger logger = LogManager.getLogger(ExpeditionEncounterExecutor.class);

    public static void execute(ExpeditionEncounter input) {
        LocationId locationId = input.getLocationId();
        Integer page = ServicePlatform.get().getExpeditionDeck().getExpeditionPage();
        logger.info("Drawing expedition " + locationId + " page=" + page);
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Expedition", locationId.toString());
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideSelectEncounterTable();
        ServicePlatform.get().getService().showLocationBackground(locationId);
        ServicePlatform.get().getEventListenerProvider().executeExpeditionEncounter(page, locationId);
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getService().discardTopExpedition();
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();
    }
}
