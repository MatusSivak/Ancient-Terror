package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;

public class EndOfEncounterAction extends AbstractHookableAction<Object, Object> {

    public EndOfEncounterAction() {
        super(BeforeAfterEvent.END_OF_ENCOUNTER);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Object> ss) {
        ServicePlatform.get().getEncounterService().checkDefeatedAfterEncounter();
        ss.onSuccess(input);
    }
}
