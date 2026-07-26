package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

/**
 * @author msivak
 */
public class TravelToLocationAction extends AbstractHookableAction<LocationInfo.Connection, InvestigatorId> {

    public TravelToLocationAction() {
        super(BeforeAfterEvent.TRAVEL_TO_LOCATION);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super InvestigatorId> ss) {
        if (input == null) {
            ss.onSuccess(null);
        }
        InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        ServicePlatform.get().getGameController().travelToLocation(input.getLocationId()).subscribe(() -> {
            activeInvestigator.setCurrentLocationId(input.getLocationId());
            ss.onSuccess(activeInvestigator.getInfo().getInvestigatorId());
        });
    }
}
