package sk.sivak.eldritchhorror.core.eventqueue.provider;

import sk.sivak.eldritchhorror.core.eventqueue.EventQueueWrite;

/**
 * @author msivak
 */
public interface EventQueueProvider {

    EventQueueWrite getEventQueue();
}
