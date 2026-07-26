package sk.sivak.eldritchhorror.core.commandqueue;

import rx.Single;
import rx.SingleSubscriber;

/**
 * @author msivak
 */
public interface Command<In, Out> {

    Single<Out> execute();

    void setInput(In input);

    String getName();

    void doWhenCompleted(Out out);

    void addOnCompletedListener(SingleSubscriber<? super Out> listener);
}
