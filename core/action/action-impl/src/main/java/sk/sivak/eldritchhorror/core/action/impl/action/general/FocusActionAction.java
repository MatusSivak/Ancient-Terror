package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.FocusData;

/**
 * @author msivak
 */
public class FocusActionAction extends AbstractHookableAction<FocusData, FocusData> {

    public FocusActionAction() {
        super(BeforeAfterEvent.FOCUS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super FocusData> ss) {
        if (input.getFocusGained() != 0) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainFocus();
            ServicePlatform.get().getService().convertTo(FocusData.class, () -> input);
            ServicePlatform.get().getService().release();
            ss.onSuccess(null);
        } else {
            ServicePlatform.get().getGameController().justFocus()
                    .subscribe(() -> ss.onSuccess(input));
        }
    }
}
