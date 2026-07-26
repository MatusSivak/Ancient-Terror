package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class DisplayChalkboardAction implements Action<Object, Object> {

    private final String text;
    private final int positionX;
    private final int positionY;
    private final boolean waitOnClick;

    public DisplayChalkboardAction(String text, int positionX, int positionY, boolean waitOnClick) {
        this.text = text;
        this.positionX = positionX;
        this.positionY = positionY;
        this.waitOnClick = waitOnClick;
    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            Completable displayChalkboard = ServicePlatform.get().getInitGameController().displayChalkboard(text, positionX, positionY);
            if (waitOnClick) {
                displayChalkboard.subscribe(() -> {
                    onSub.onSuccess(null);
                });
            } else {
                displayChalkboard.subscribe();
                onSub.onSuccess(null);
            }
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
