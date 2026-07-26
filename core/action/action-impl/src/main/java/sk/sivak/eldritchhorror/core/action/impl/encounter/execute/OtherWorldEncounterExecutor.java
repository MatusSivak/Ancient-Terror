package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.OtherWorldEncounter;

import static sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory.ENCOUNTER;

public class OtherWorldEncounterExecutor {
    public static void execute(OtherWorldEncounter input) {
        Integer page = ServicePlatform.get().getGateStack().drawGateEncounter();
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(ENCOUNTER, "OtherWorld");


        ServicePlatform.get().getService().hold();
        if (input.isHideEncounterTable()) {
            ServicePlatform.get().getService().hideSelectEncounterTable();
        }
        ServicePlatform.get().getService().showOtherWorldBackground();
        ServicePlatform.get().getService().swapInvestigatorAndGateLayer();
        ServicePlatform.get().getEventListenerProvider().executeOtherWorldEncounter(page);
        ServicePlatform.get().getEncounterService().endOfEncounter(input);
        ServicePlatform.get().getService().swapInvestigatorAndGateLayer();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getEncounterService().insertDefeatSequencePoint();
        ServicePlatform.get().getService().release();
    }
}
