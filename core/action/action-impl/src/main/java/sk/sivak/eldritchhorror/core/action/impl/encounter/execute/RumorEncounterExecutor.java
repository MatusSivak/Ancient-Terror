package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.RumorEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class RumorEncounterExecutor {
    public static void execute(RumorEncounter encounter) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Rumor", encounter.getSecondLine());
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEventQueue().fireDirectEvent(DirectEvent.ENCOUNTER_ONGOING_RUMOR, encounter);
        ServicePlatform.get().getEncounterService().endOfEncounter(encounter);
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();

    }
}
