package sk.sivak.eldritchhorror.core.action;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

/**
 * @author msivak
 */
public abstract class AbstractHookableAction<In, Out> implements HookableAction<In, Out> {

    protected In input;
    protected BeforeAfterEvent beforeAfterEvent;
    protected SingleSubscriber<? super Out> ss;

    public AbstractHookableAction(BeforeAfterEvent beforeAfterEvent) {
        this.beforeAfterEvent = beforeAfterEvent;
    }

    @Override
    public Single<Out> execute() {
        Single<Out> single = Single.create(onSub -> {
            this.ss = onSub;
            onExecute(ss);
        });
        return single.cache();
    }

    protected abstract void onExecute(SingleSubscriber<? super Out> ss);

    @Override
    public void setInput(In input) {
        this.input = input;
    }

    public BeforeAfterEvent getBeforeAfterEvent() {
        return beforeAfterEvent;
    }
}
