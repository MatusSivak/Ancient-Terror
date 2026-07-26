package sk.sivak.eldritchhorror.core.eventlistener;

import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

/**
 * @author msivak
 */
public abstract class EventListenerImpl<DATA> implements EventListener<DATA> {

    public void registerBeforeEventListener(BeforeAfterEvent beforeEvent) {
        ServicePlatform.get().getEventQueue().addBeforeEventListener(this, beforeEvent);
    }

    public void registerAfterEventListener(BeforeAfterEvent afterEvent) {
        ServicePlatform.get().getEventQueue().addAfterEventListener(this, afterEvent);
    }

    public void registerDirectEventListener(DirectEvent directEvent) {
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, directEvent);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
