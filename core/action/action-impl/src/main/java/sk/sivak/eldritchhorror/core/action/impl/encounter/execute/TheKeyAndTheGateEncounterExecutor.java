package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class TheKeyAndTheGateEncounterExecutor {
    public static void execute(MysteryEncounter input) {
        Integer page = ServicePlatform.get().getVortexes().drawTheKeyAndTheGateEncounter();
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "The Key and the Gate");

        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideSelectEncounterTable();
        ServicePlatform.get().getService().showTheKeyAndTheGateBackground();
        ServicePlatform.get().getEventListenerProvider().executeTheKeyAndTheGateEncounter(page);
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();
    }
}
