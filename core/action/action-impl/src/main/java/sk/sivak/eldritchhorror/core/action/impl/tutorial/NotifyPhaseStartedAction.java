package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

public class NotifyPhaseStartedAction implements Action<Object, Object> {

    private final String phaseName;

    public NotifyPhaseStartedAction(String phaseName) {
        this.phaseName = phaseName;
    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.TUTORIAL, "PHASE", phaseName);
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
