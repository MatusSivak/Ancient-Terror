package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;

public class CloseGateAction extends AbstractHookableAction<Object, CloseGateData> {

    private final LocationId location;
    private final boolean isOtherworldEncounter;

    public CloseGateAction(LocationId location, boolean isOtherworldEncounter) {
        super(BeforeAfterEvent.CLOSE_GATE);
        this.location = location;
        this.isOtherworldEncounter = isOtherworldEncounter;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CloseGateData> ss) {
        GateColor gateColor = ServicePlatform.get().getGateStack().closeGate(location);
        ServicePlatform.get().getGameController().closeGate(location).subscribe(() -> {
            ss.onSuccess(new CloseGateData(location, gateColor, isOtherworldEncounter));
        });
    }
}
