package sk.sivak.eldritchhorror.core.action;

import rx.Single;

/**
 * @author msivak
 */
public class InitValueActionImpl<InOut> implements InitValueAction<InOut> {

    private InOut value;

    public InitValueActionImpl(InOut value) {
        this.value = value;
    }

    @Override
    public Single<InOut> execute() {
        Single<InOut> single = Single.fromCallable(() -> value);
        return single.cache();
    }

    @Override
    public void setInput(Object input) {
        // ignored
    }

}
