package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class LostInTimeAndSpaceAction extends AbstractHookableAction<InvestigatorId, InvestigatorId> {

    public LostInTimeAndSpaceAction() {
        super(BeforeAfterEvent.LOST_IN_TIME_AND_SPACE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super InvestigatorId> ss) {
        ServicePlatform.get().getInvestigators().lostInTimeAndSpace(input);
        ServicePlatform.get().getPerformedActions().clearFreeActions(input);
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getInvestigator(input).getCurrentLocationId();
        ServicePlatform.get().getGameController().removeInvestigator(input, currentLocationId).subscribe(() -> {
            ServicePlatform.get().getInvestigators().getInvestigator(input).setCurrentLocationId(null);
            ss.onSuccess(input);
        });
    }
}
