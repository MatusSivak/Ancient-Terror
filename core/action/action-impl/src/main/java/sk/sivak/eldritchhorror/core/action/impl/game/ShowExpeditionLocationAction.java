package sk.sivak.eldritchhorror.core.action.impl.game;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;

/**
 * @author msivak
 */
public class ShowExpeditionLocationAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(ShowExpeditionLocationAction.class);

    @Override
    public Single<Void> execute() {
        Single<Void> single = Single.create(onSub());
        return single.cache();
    }

    private Single.OnSubscribe<Void> onSub() {
        if (!GoogleServicesHolder.isTutorialPassed()) {
            return sub -> {
                ServicePlatform.get().getGameController().confirmExpeditionLocation().subscribe();
                sub.onSuccess(null);
            };
        }
        return sub -> ServicePlatform.get().getGameController().confirmExpeditionLocation().subscribe(() -> {
            logger.debug("Expedition location confirmed.");
            sub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {
        // input is ignored
    }
}
