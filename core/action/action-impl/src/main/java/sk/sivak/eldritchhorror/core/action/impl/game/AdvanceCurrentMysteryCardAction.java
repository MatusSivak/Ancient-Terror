package sk.sivak.eldritchhorror.core.action.impl.game;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Observable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author msivak
 */
public class AdvanceCurrentMysteryCardAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(AdvanceCurrentMysteryCardAction.class);
    private final int amount;

    public AdvanceCurrentMysteryCardAction(int amount) {
        this.amount = amount;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            Integer mysteryComplexity = ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard().getMysteryComplexity();
            Integer progress = ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard().getProgress();
            if (progress-amount >= mysteryComplexity) {
                ServicePlatform.get().getGameController().showCurrentMysteryCard(false)
                        .subscribe(() -> {
                    onSub.onSuccess(null);
                }, logger::error);
            } else {
                ServicePlatform.get().getGameController().advanceCurrentMysteryCard(amount).subscribe(() -> {
                    onSub.onSuccess(null);
                }, logger::error);
            }
        });

    }

    @Override
    public void setInput(Object input) {
        // input is ignored
    }
}
