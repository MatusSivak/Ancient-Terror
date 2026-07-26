package sk.sivak.eldritchhorror.core.action;

import rx.Single;

/**
 * @author msivak
 */
public interface Action<In, Out> {

    Single<Out> execute();

    void setInput(In input);
}
