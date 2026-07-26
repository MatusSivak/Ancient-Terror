package sk.sivak.eldritchhorror.core.action.impl.game;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

/**
 * @author msivak
 */
public class SpawnRedPinsAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(SpawnRedPinsAction.class);

    @Override
    public Single<Void> execute() {
        Single<Void> single = Single.create(onSub());
        return single.cache();
    }

    private Single.OnSubscribe<Void> onSub() {
        return sub -> {
            ServicePlatform.get().getGameController().spawnRedPins().subscribe(() -> {
                logger.debug("Red pins spawned");
                sub.onSuccess(null);
            });
        };
    }

    @Override
    public void setInput(Object input) {
        // input is ignored
    }
}
