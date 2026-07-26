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
public class RestartGameAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(RestartGameAction.class);

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().restartGame();
        });
    }

    @Override
    public void setInput(Object input) {
        // input is ignored
    }
}
