package sk.sivak.eldritchhorror.core.action.impl.game;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

import java.util.concurrent.TimeUnit;

/**
 * @author msivak
 */
public class ShowCurrentMysteryCardAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(ShowCurrentMysteryCardAction.class);
    private boolean moveCamera;
    private final boolean quick;

    public ShowCurrentMysteryCardAction(boolean moveCamera, boolean quick) {
        this.moveCamera = moveCamera;
        this.quick = quick;
    }

    @Override
    public Single<Void> execute() {
        if (quick) {
            return Single.just(null);
        }
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().showCurrentMysteryCard(moveCamera)
                    .subscribe(() -> {
                        logger.debug("Mystery card '" + ServicePlatform.get().getMysteryDeck().getCurrentMysteryCard().getName() + "' shown");
                        onSub.onSuccess(null);
                    }, logger::error);
        });

    }

    @Override
    public void setInput(Object input) {
        // input is ignored
    }
}
