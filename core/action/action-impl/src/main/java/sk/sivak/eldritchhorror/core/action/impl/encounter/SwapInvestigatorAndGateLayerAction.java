package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;

public class SwapInvestigatorAndGateLayerAction implements Action<Object, Object> {


    private Object input;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().swapInvestigatorAndGateLayer();
            onSub.onSuccess(input);
        });
    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
