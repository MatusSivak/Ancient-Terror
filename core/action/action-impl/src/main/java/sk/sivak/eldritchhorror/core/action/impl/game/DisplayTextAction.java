package sk.sivak.eldritchhorror.core.action.impl.game;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class DisplayTextAction implements Action<Object, Object> {

    private final String text;
    private Object input;

    public DisplayTextAction(String text) {
        this.text = text;
    }

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().displayText(text);
            onSub.onSuccess(input);
        });
    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
