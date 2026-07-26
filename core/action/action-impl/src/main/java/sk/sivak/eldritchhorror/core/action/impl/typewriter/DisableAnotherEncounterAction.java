package sk.sivak.eldritchhorror.core.action.impl.typewriter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class DisableAnotherEncounterAction implements Action<Object, Void> {

    private final InvestigatorId investigatorId;

    public DisableAnotherEncounterAction(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getPerformedEncounters().disableAnotherEncounter(investigatorId);
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {
        //
    }
}
