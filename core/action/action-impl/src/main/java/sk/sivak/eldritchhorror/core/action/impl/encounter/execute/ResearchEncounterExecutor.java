package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ResearchEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class ResearchEncounterExecutor {

    private static final Logger logger = LogManager.getLogger(ResearchEncounterExecutor.class);

    public static void execute(ResearchEncounter input) {
        LocationType locationType = input.getLocationType();
        Integer page = ServicePlatform.get().getResearchEncounterDeck().drawCard(locationType);
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Research", locationType.getValue());
        logger.info("Drawing " + locationType + " page=" + page);

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideSelectEncounterTable();
        ServicePlatform.get().getService().showResearchBackground(input.getLocationType());
        ServicePlatform.get().getEventListenerProvider().executeResearchEncounter(page, locationType, input.getLocationId());
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();
    }
}
