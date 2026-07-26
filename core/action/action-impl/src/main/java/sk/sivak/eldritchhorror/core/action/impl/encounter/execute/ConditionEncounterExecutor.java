package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ConditionEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class ConditionEncounterExecutor {
    public static void execute(ConditionEncounter input) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Condition", input.getConditionInfo().getName());
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEventListenerProvider().executeConditionBack(input.getConditionInfo());
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();
    }
}
