package sk.sivak.eldritchhorror.core.action.impl.encounter;

import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;

public class DisableEncountersEventAction extends AbstractDirectEventAction<AvailableEncounters>{

    public DisableEncountersEventAction() {
        super(DirectEvent.DISABLE_ENCOUNTERS);
    }
}
