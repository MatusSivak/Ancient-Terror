package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;

/**
 * @author msivak
 */
public class GainClueFromSpaceAction extends AbstractHookableAction<GainClueData, GainClueData> {

    public GainClueFromSpaceAction() {
        super(BeforeAfterEvent.GAIN_CLUE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super GainClueData> ss) {
        if (input.getInvestigatorId() == null) {
            input.setInvestigatorId(ServicePlatform.get().getInvestigators().getActiveInvestigator().getInfo().getInvestigatorId());
        }
        if (input.getLocationId() == null) {
            input.setLocationId(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
        }
        LocationId spawnLocationId = ServicePlatform.get().getCluePool().gainClueFromSpace(input.getInvestigatorId(), input.getLocationId());
        ServicePlatform.get().getGameController().gainClueFromSpace(spawnLocationId)
                .subscribe(() -> ss.onSuccess(input));

    }
}
