package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;

public class HighlightRumorReckoningAction implements Action<Object, Void> {

    private final String rumorId;

    public HighlightRumorReckoningAction(String rumorId) {
        this.rumorId = rumorId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            RumorCardInfo activeRumor = ServicePlatform.get().getRumors().getActiveRumor(rumorId);
            Completable showRumorCard = ServicePlatform.get().getGameController().justShowRumorCard(activeRumor);
            Completable hideRumorCard = ServicePlatform.get().getGameController().justHideRumorCard();
            Completable highlightRumorReckoning = Completable.defer(() -> ServicePlatform.get().getGameController().highlightRumorReckoning());
            showRumorCard.andThen(highlightRumorReckoning).andThen(hideRumorCard).subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
