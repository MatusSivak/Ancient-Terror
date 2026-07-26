package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;

public class InsertDefeatSequencePointAction implements Action<Object, Object> {

    private Object input;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            onSub.onSuccess(input);
        });
    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
