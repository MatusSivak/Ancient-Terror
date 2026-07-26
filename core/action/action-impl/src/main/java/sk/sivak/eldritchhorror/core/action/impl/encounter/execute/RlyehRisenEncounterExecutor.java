package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.OtherWorldEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class RlyehRisenEncounterExecutor {
    public static void execute(MysteryEncounter input) {
        Integer page = ServicePlatform.get().getVortexes().drawRlyehRisenEncounter();
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "R'lyeh Risen");

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideSelectEncounterTable();
        ServicePlatform.get().getService().showRlyehRisenBackground();
        ServicePlatform.get().getEventListenerProvider().executeRlyehRisenEncounter(page);
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();
    }
}
