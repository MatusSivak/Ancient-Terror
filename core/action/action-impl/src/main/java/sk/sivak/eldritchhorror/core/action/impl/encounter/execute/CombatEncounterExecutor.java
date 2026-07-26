package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.CombatEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class CombatEncounterExecutor {
    public static void execute(CombatEncounter input) {
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "Combat", input.getMonsterInfo().getName());
        ServicePlatform.get().getTestService().hold();
        ServicePlatform.get().getService().hideSelectEncounterTable();
        ServicePlatform.get().getTestService().combat(input.getMonsterInfo());
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getTestService().release();
    }
}
