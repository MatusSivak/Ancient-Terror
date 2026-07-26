package sk.sivak.eldritchhorror.core.eventqueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java8.features.util.MapUtils.computeIfAbsent;

/**
 * @author msivak
 */
public class EventQueueImpl implements EventQueueWrite {

    private static final Logger logger = LogManager.getLogger(EventQueueImpl.class);

    private Map<BeforeAfterEvent, List<EventListener>> beforeListenersMap;
    private Map<BeforeAfterEvent, List<EventListener>> afterListenersMap;
    private Map<DirectEvent, List<EventListener>> directListenersMap;

    public EventQueueImpl() {
        init();
    }

    public <DATA> void addBeforeEventListener(EventListener<DATA> eventListener, BeforeAfterEvent beforeAfterEvent) {
        validateMatchingClasses(beforeAfterEvent.getInputClass(), eventListener.getDataClass());
        List<EventListener> beforeListeners = getBeforeListeners(beforeAfterEvent);
        beforeListeners.add(eventListener);
        logger.info("New Before-Event listener registered: " + eventListener.getClass().getSimpleName());
    }

    public <DATA> void addAfterEventListener(EventListener<DATA> eventListener, BeforeAfterEvent beforeAfterEvent) {
        validateMatchingClasses(beforeAfterEvent.getOutputClass(), eventListener.getDataClass());
        List<EventListener> afterListeners = getAfterListeners(beforeAfterEvent);
        afterListeners.add(eventListener);
        logger.info("New After-Event listener registered: " + eventListener.getClass().getSimpleName());
    }

    @Override
    public <DATA> void addDirectEventListener(EventListener<DATA> eventListener, DirectEvent directEvent) {
        validateMatchingClasses(directEvent.getDataClass(), eventListener.getDataClass());
        List<EventListener> directListeners = getDirectListeners(directEvent);
        directListeners.add(eventListener);
        logger.info("New Direct-Event listener registered: " + eventListener.getClass().getSimpleName());
    }

    @Override
    public <DATA> void fireBeforeEvent(BeforeAfterEvent beforeAfterEvent, DATA data) {
        if (data != null) {
            validateMatchingClasses(beforeAfterEvent.getInputClass(), data.getClass());
        }
        logger.debug("Before-Event fired: " + beforeAfterEvent);
        fireBeforeEvent(data, beforeAfterEvent);
    }

    @Override
    public <DATA> void fireAfterEvent(BeforeAfterEvent beforeAfterEvent, DATA data) {
        if (data != null) {
            validateMatchingClasses(beforeAfterEvent.getOutputClass(), data.getClass());
        }
        logger.debug("After-Event fired: " + beforeAfterEvent);
        fireAfterEvent(data, beforeAfterEvent);
    }

    @Override
    public <DATA> void fireDirectEvent(DirectEvent directEvent, DATA data) {
        if (data != null) {
            validateMatchingClasses(directEvent.getDataClass(), data.getClass());
        }
        logger.debug("Direct-Event fired: " + directEvent);
        fireDirectEvent(data, directEvent);
    }

    @Override
    public void unregisterListener(EventListener eventListener) {
        for (Map.Entry<BeforeAfterEvent, List<EventListener>> entry : beforeListenersMap.entrySet()) {
            if (entry.getValue().remove(eventListener)) {
                logger.debug("Before-Event listener '" + eventListener + "' removed for event '" + entry.getKey() + "'");
            }
        }
        for (Map.Entry<BeforeAfterEvent, List<EventListener>> entry : afterListenersMap.entrySet()) {
            if (entry.getValue().remove(eventListener)) {
                logger.debug("After-Event listener '" + eventListener + "' removed for event '" + entry.getKey() + "'");
            }
        }
        for (Map.Entry<DirectEvent, List<EventListener>> entry : directListenersMap.entrySet()) {
            if (entry.getValue().remove(eventListener)) {
                logger.debug("Direct-Event listener '" + eventListener + "' removed for event '" + entry.getKey() + "'");
            }
        }
    }

    @Override
    public void init() {
        beforeListenersMap = new HashMap<>();
        afterListenersMap = new HashMap<>();
        directListenersMap = new HashMap<>();
    }

    private <DATA> void validateMatchingClasses(Class<?> eventTypeClass, Class<DATA> data) {
        if (!eventTypeClass.equals(data) && !eventTypeClass.isAssignableFrom(data)) {
            throw new IllegalArgumentException("Class mismatch. Event type class: " + eventTypeClass + ", data class: " + data);
        }
    }

    private <DATA> void fireBeforeEvent(DATA data, BeforeAfterEvent beforeAfterEvent) {
        List<EventListener> eventListeners = beforeListenersMap.get(beforeAfterEvent);
        doWithListeners(data, eventListeners);
    }

    private <DATA> void fireAfterEvent(DATA data, BeforeAfterEvent beforeAfterEvent) {
        List<EventListener> eventListeners = afterListenersMap.get(beforeAfterEvent);
        doWithListeners(data, eventListeners);
    }

    private <DATA> void fireDirectEvent(DATA data, DirectEvent directEvent) {
        List<EventListener> eventListeners = directListenersMap.get(directEvent);
        doWithListeners(data, eventListeners);
    }

    private <DATA> void doWithListeners(DATA data, List<EventListener> eventListeners) {
        if (eventListeners == null) {
            logger.debug("No event listeners found.");
            return;
        }
        for (EventListener eventListener : eventListeners) {
            logger.info("Invoking event listener '" + eventListener + "' with data '" + data + "'");
            eventListener.onNotify(data);
        }
    }

    private List<EventListener> getBeforeListeners(BeforeAfterEvent beforeAfterEvent) {
        return computeIfAbsent(beforeListenersMap, beforeAfterEvent, k -> new LinkedList<>());
    }

    private List<EventListener> getAfterListeners(BeforeAfterEvent beforeAfterEvent) {
        return computeIfAbsent(afterListenersMap, beforeAfterEvent, k -> new LinkedList<>());
    }

    private List<EventListener> getDirectListeners(DirectEvent directEvent) {
        return computeIfAbsent(directListenersMap, directEvent, k -> new LinkedList<>());
    }

}
