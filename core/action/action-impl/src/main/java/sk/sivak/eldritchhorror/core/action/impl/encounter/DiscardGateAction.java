package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardGateData;

public class DiscardGateAction extends AbstractHookableAction<Object, DiscardGateData> {

    private final LocationId location;

    public DiscardGateAction(LocationId location) {
        super(BeforeAfterEvent.DISCARD_GATE);
        this.location = location;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DiscardGateData> ss) {
        GateColor gateColor = ServicePlatform.get().getGateStack().closeGate(location);
        ServicePlatform.get().getGameController().closeGate(location).subscribe(() -> {
            ss.onSuccess(new DiscardGateData(location, gateColor));
        });
    }
}
