package sk.sivak.eldritchhorror.core.action.impl.encounter;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;

public class InvestigatorFinishedEncounterAction extends AbstractDirectEventAction<EncounterResult>{

    public InvestigatorFinishedEncounterAction() {
        super(DirectEvent.INVESTIGATOR_FINISHED_ENCOUNTER);
    }
}
