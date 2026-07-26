package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class RemoveInvestigatorFromPlaceAction implements Action<Object, Void> {

    private final InvestigatorId investigatorId;

    public RemoveInvestigatorFromPlaceAction(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getCurrentLocationId();
            ServicePlatform.get().getGameController().removeInvestigator(investigatorId, currentLocationId).subscribe(() -> {
                ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).setCurrentLocationId(null);
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
