package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class RetreatDoomAction extends AbstractHookableAction<Object, Object> {

    public RetreatDoomAction() {
        super(BeforeAfterEvent.RETREAT_DOOM);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Object> ss) {
        ServicePlatform.get().getDoomTrack().retreatDoom();
        int doom = ServicePlatform.get().getDoomTrack().getCurrentDoom();
        ServicePlatform.get().getDoomOmenController().retreatDoom(doom, 1).subscribe(() -> ss.onSuccess(input));
    }
}
