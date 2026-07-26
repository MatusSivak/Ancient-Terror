package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

public class HighlightRumorObjectiveAction implements Action<Object, Void> {

    private final String rumorId;

    public HighlightRumorObjectiveAction(String rumorId) {
        this.rumorId = rumorId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            RumorCardInfo activeRumor = ServicePlatform.get().getRumors().getActiveRumor(rumorId);
            Completable showRumorCard = ServicePlatform.get().getGameController().justShowRumorCard(activeRumor);
            Completable highlightRumorObjective = Completable.defer(() -> ServicePlatform.get().getGameController().highlightRumorObjective());
            Completable tearRumorCardApart = ServicePlatform.get().getMonsterController().tearRumorCardApart();
            showRumorCard.andThen(highlightRumorObjective).andThen(tearRumorCardApart).subscribe(() -> {
                GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.RUMOR_ENDED, "solved", rumorId);
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
