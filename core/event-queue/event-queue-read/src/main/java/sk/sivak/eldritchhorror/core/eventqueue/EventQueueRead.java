package sk.sivak.eldritchhorror.core.eventqueue;

import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

import java.util.List;

/**
 * @author msivak
 */
public interface EventQueueRead {

    <DATA> void addBeforeEventListener(EventListener<DATA> eventListener, BeforeAfterEvent beforeAfterEvent);

    <DATA> void addAfterEventListener(EventListener<DATA> eventListener, BeforeAfterEvent beforeAfterEvent);

    <DATA> void addDirectEventListener(EventListener<DATA> eventListener, DirectEvent directEvent);

    void unregisterListener(EventListener eventListener);

}
