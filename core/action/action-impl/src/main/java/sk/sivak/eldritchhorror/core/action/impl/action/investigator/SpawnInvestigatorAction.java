package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class SpawnInvestigatorAction extends AbstractHookableAction<Object, InvestigatorId> {

    private final InvestigatorId investigatorId;
    private final LocationId locationId;

    public SpawnInvestigatorAction(InvestigatorId investigatorId, LocationId locationId) {
        super(BeforeAfterEvent.SPAWN_INVESTIGATOR);
        this.investigatorId = investigatorId;
        this.locationId = locationId;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super InvestigatorId> ss) {
        ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).setLostInTimeAndSpace(false);
        ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).setCurrentLocationId(locationId);
        ServicePlatform.get().getGameController().spawnInvestigator(investigatorId, locationId).subscribe(() -> {
            ss.onSuccess(investigatorId);
        });
    }
}
