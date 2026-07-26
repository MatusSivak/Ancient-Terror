package sk.sivak.eldritchhorror.core.action.impl.phase;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class ChangePhaseAction extends AbstractHookableAction<Object, PhaseType> {

    public ChangePhaseAction() {
        super(BeforeAfterEvent.CHANGE_PHASE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super PhaseType> ss) {
        ServicePlatform.get().getPhase().changePhase();
        ss.onSuccess(ServicePlatform.get().getPhase().getPhaseType());
    }
}
