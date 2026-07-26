package sk.sivak.eldritchhorror.core.commandqueue;

import rx.SingleSubscriber;

import java.util.LinkedList;
import java.util.List;

import static java8.features.util.IterableUtils.forEach;

/**
 * @author msivak
 */
public abstract class AbstractCommand<In, Out> implements Command<In, Out> {

    protected In input;
    protected List<SingleSubscriber<? super Out>> onCompletedListeners = new LinkedList<>();

    @Override
    public void setInput(In input) {
        this.input = input;
    }

    @Override
    public void doWhenCompleted(Out out) {
        forEach(onCompletedListeners, listener -> listener.onSuccess(out));
    }

    @Override
    public void addOnCompletedListener(SingleSubscriber<? super Out> listener) {
        onCompletedListeners.add(listener);
    }
}
