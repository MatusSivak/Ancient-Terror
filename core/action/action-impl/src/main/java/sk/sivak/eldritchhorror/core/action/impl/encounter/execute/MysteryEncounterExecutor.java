package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class MysteryEncounterExecutor {
    public static void execute(MysteryEncounter input) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Mystery", input.getSecondLine());
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEventQueue().fireDirectEvent(DirectEvent.ENCOUNTER_ACTIVE_MYSTERY, input);
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();

    }
}
