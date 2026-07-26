package sk.sivak.eldritchhorror.core.action.impl.initgame;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

/**
 * @author msivak
 */
public class ToGameScreenAction implements Action<Void, Void> {

    @Override
    public Single<Void> execute() {
        return Single.fromCallable(() -> {
            ServicePlatform.get().getInitGameController().toGameScreen();
            ServicePlatform.get().getGameController().setClearQueueAction(() -> ServicePlatform.get().getService().clearQueue());
            ServicePlatform.get().getGameController().startGame();
            return null;
        });
    }

    @Override
    public void setInput(Void input) {
        // ignored
    }
}
