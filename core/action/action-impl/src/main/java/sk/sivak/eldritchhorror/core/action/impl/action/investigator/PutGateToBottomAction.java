package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;

public class PutGateToBottomAction implements Action<Object, Void> {

    private final GateInfo selectedGate;

    public PutGateToBottomAction(GateInfo selectedGate) {
        this.selectedGate = selectedGate;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGateStack().putGateToBottom(selectedGate);
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
