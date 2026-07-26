package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class AdvanceDoomAction extends AbstractHookableAction<Integer, Void> {

    public AdvanceDoomAction() {
        super(BeforeAfterEvent.ADVANCE_DOOM);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        int previousDoom = ServicePlatform.get().getDoomTrack().getCurrentDoom();
        for (int i = 0; i < input; i++) {
            ServicePlatform.get().getDoomTrack().advanceDoom();
        }
        int currentDoom = ServicePlatform.get().getDoomTrack().getCurrentDoom();

        if (currentDoom <=0) {
            ServicePlatform.get().getDoomOmenController().advanceDoom(0, -previousDoom)
                    .subscribe(() -> ss.onSuccess(null));
        } else {
            ServicePlatform.get().getDoomOmenController().advanceDoom(currentDoom, -input)
                    .subscribe(() -> ss.onSuccess(null));
        }
    }
}
