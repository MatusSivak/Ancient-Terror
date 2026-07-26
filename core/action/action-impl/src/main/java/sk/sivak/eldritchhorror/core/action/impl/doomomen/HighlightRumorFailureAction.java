package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

public class HighlightRumorFailureAction implements Action<Object, Void> {

    private final String rumorId;

    public HighlightRumorFailureAction(String rumorId) {
        this.rumorId = rumorId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            Completable highlightRumorFailure = ServicePlatform.get().getGameController().highlightRumorFailure();
            Completable tearRumorCardApart = ServicePlatform.get().getMonsterController().tearRumorCardApart();
            highlightRumorFailure.andThen(tearRumorCardApart).subscribe(() -> {
                GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.RUMOR_ENDED, "failed", rumorId);
                ServicePlatform.get().getGameController().clearStorm(rumorId);
                ServicePlatform.get().getRumors().removeActiveRumor(rumorId);
                ServicePlatform.get().getGameController().updateRumorButtonVisibility();
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
