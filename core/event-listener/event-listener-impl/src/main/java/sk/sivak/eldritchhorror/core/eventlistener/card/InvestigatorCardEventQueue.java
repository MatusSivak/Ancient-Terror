package sk.sivak.eldritchhorror.core.eventlistener.card;

import java8.features.function.Supplier;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueRead;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

import java.util.IdentityHashMap;
import java.util.Map;

/** Keeps card registrations intact while their owner is off the board. */
public final class InvestigatorCardEventQueue implements EventQueueRead {
    private final EventQueueRead delegate;
    private final Supplier<InvestigatorId> owner;
    private final Map<EventListener, EventListener> guardedListeners = new IdentityHashMap<>();

    public InvestigatorCardEventQueue(EventQueueRead delegate, Supplier<InvestigatorId> owner) {
        this.delegate = delegate;
        this.owner = owner;
    }

    private <T> EventListener<T> guard(EventListener<T> listener) {
        EventListener<T> guarded = guardedListeners.get(listener);
        if (guarded == null) {
            guarded = new EventListener<T>() {
                @Override
                public void onNotify(T data) {
                    if (!ServicePlatform.get().getInvestigators().getInvestigator(owner.get()).isLostInTimeAndSpace()) {
                        listener.onNotify(data);
                    }
                }

                @Override
                public Class<T> getDataClass() {
                    return listener.getDataClass();
                }
            };
            guardedListeners.put(listener, guarded);
        }
        return guarded;
    }

    @Override
    public <T> void addBeforeEventListener(EventListener<T> listener, BeforeAfterEvent event) {
        delegate.addBeforeEventListener(guard(listener), event);
    }

    @Override
    public <T> void addAfterEventListener(EventListener<T> listener, BeforeAfterEvent event) {
        delegate.addAfterEventListener(guard(listener), event);
    }

    @Override
    public <T> void addDirectEventListener(EventListener<T> listener, DirectEvent event) {
        // Refresh exhausted cards normally, so returning investigators do not miss a refresh.
        delegate.addDirectEventListener(event == DirectEvent.REENABLE_DISABLED_ABILITIES ? listener : guard(listener), event);
    }

    @Override
    public void unregisterListener(EventListener listener) {
        EventListener guarded = guardedListeners.remove(listener);
        if (guarded != null) {
            delegate.unregisterListener(guarded);
        }
        delegate.unregisterListener(listener);
    }
}
