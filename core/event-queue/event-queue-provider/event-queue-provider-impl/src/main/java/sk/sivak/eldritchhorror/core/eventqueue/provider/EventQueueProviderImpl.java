package sk.sivak.eldritchhorror.core.eventqueue.provider;

import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueWrite;

/**
 * @author msivak
 */
public class EventQueueProviderImpl implements EventQueueProvider {

    private EventQueueImpl eventQueue;

    public EventQueueProviderImpl() {
        eventQueue = new EventQueueImpl();
    }

    @Override
    public EventQueueWrite getEventQueue() {
        return eventQueue;
    }
}
