package sk.sivak.eldritchhorror.core.action.impl.test;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class UnsetTestFlavorAction implements Action<Object, Object> {

    private Object input;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getTestFlavor().endFlavor();
            onSub.onSuccess(input);
        });
    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
