package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;

public class CountdownRumorCardAction implements Action<Object, Void> {

    private final String rumorId;
    private int amount;

    public CountdownRumorCardAction(String rumorId, int amount) {
        this.rumorId = rumorId;
        this.amount = amount;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {

            RumorCardInfo activeRumor = ServicePlatform.get().getRumors().getActiveRumor(rumorId);
            amount = Math.min(activeRumor.getTimeRemaining(), amount);

            Completable showRumorCard = ServicePlatform.get().getGameController().justShowRumorCard(activeRumor);
            Completable countdownRumorCard = Completable.defer(() ->ServicePlatform.get().getGameController().countdownRumorCard(amount));

            if (activeRumor.getTimeRemaining() - amount > 0) {
                Completable hideRumorCard = ServicePlatform.get().getGameController().justHideRumorCard();
                showRumorCard.andThen(countdownRumorCard).andThen(hideRumorCard).subscribe(() -> {
                    ServicePlatform.get().getRumors().countdown(rumorId, amount);
                    onSub.onSuccess(null);
                });
            } else {
                showRumorCard.andThen(countdownRumorCard).subscribe(() -> {

                    ServicePlatform.get().getRumors().countdown(rumorId, amount);
                    onSub.onSuccess(null);
                });
            }
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
