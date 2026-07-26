package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class AdvanceOmenAction extends AbstractHookableAction<Object, OmenInfo> {

    public AdvanceOmenAction() {
        super(BeforeAfterEvent.ADVANCE_OMEN);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super OmenInfo> ss) {
        ServicePlatform.get().getOmenTrack().advanceOmen();
        OmenInfo currentOmen = ServicePlatform.get().getOmenTrack().getCurrentOmen();
        ServicePlatform.get().getDoomOmenController().advanceOmen(currentOmen).subscribe(() -> {
            ss.onSuccess(ServicePlatform.get().getOmenTrack().getCurrentOmen());
        });
    }
}
