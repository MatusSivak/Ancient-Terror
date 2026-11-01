package sk.sivak.eldritchhorror.core.action.impl.phase;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class ShowPhaseAction extends AbstractHookableAction<Object, Void> {

    public ShowPhaseAction() {
        super(BeforeAfterEvent.SHOW_PHASE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        proceedWithPhase(ss);
    }

    private void proceedWithPhase(SingleSubscriber<? super Void> ss) {
        PhaseType phaseType = ServicePlatform.get().getPhase().getPhaseType();
        ServicePlatform.get().getGameController().showPhase(phaseType).subscribe(() -> ss.onSuccess(null));
    }
}