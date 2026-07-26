package sk.sivak.eldritchhorror.core.action.impl.game;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ViewProperties;

/**
 * @author msivak
 */
public class ShowAncientOneCardAction implements Action<Object, Void> {

    private static final Logger logger = LogManager.getLogger(ShowAncientOneCardAction.class);

    @Override
    public Single<Void> execute() {

        if (ViewProperties.MODE == ViewProperties.MODE_TEST) {
            return Single.just(null);
        }
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().showAncientOneCard().subscribe(() -> {
                logger.debug("Ancient one card shown");
                onSub.onSuccess(null);
            }, logger::error);
        });

    }

    @Override
    public void setInput(Object input) {
        // input is ignored
    }
}
