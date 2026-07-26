package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ConditionEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class SkipEncounterExecutor {
    public static void execute() {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Skip");
        ServicePlatform.get().getService().hideSelectEncounterTable();
    }
}
