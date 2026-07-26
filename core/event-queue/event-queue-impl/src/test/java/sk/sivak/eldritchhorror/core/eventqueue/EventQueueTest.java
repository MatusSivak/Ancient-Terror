package sk.sivak.eldritchhorror.core.eventqueue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;

/**
 * @author msivak
 */
public class EventQueueTest {

    private static final Logger logger = LogManager.getLogger(EventQueueTest.class);

    @Test
    public void noListeners() {
        EventQueueWrite eventQueue = new EventQueueImpl();

        eventQueue.addAfterEventListener(new FirstEventListener(), BeforeAfterEvent.LOSE_SANITY);
        eventQueue.addAfterEventListener(new SecondEventListener(), BeforeAfterEvent.LOSE_SANITY);

        eventQueue.fireBeforeEvent(BeforeAfterEvent.LOSE_SANITY, new LoseTokenData(1, TokenType.SANITY));

    }

    @Test
    public void simpleBeforeEventQueueTest() {
        EventQueueWrite eventQueue = new EventQueueImpl();

        eventQueue.addBeforeEventListener(new FirstEventListener(), BeforeAfterEvent.LOSE_SANITY);
        eventQueue.addBeforeEventListener(new SecondEventListener(), BeforeAfterEvent.LOSE_SANITY);

        eventQueue.fireBeforeEvent(BeforeAfterEvent.LOSE_SANITY, new LoseTokenData(1, TokenType.SANITY));
    }

    @Test
    public void simpleAfterEventQueueTest() {
        EventQueueWrite eventQueue = new EventQueueImpl();

        eventQueue.addAfterEventListener(new FirstEventListener(), BeforeAfterEvent.LOSE_SANITY);
        eventQueue.addAfterEventListener(new SecondEventListener(), BeforeAfterEvent.LOSE_SANITY);

        eventQueue.fireAfterEvent(BeforeAfterEvent.LOSE_SANITY, new LoseTokenData(1, TokenType.SANITY));
    }

    @Test
    public void simpleDirectEventQueueTest() {
        EventQueueWrite eventQueue = new EventQueueImpl();

        eventQueue.addDirectEventListener(new DirectEventListener(), DirectEvent.INIT_INVESTIGATORS);

        eventQueue.fireDirectEvent(DirectEvent.INIT_INVESTIGATORS, InvestigatorId.THE_BOUNTY_HUNTER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void classesMismatch() {
        EventQueueWrite eventQueue = new EventQueueImpl();

        eventQueue.addAfterEventListener(new FirstEventListener(), BeforeAfterEvent.LOSE_SANITY);

        eventQueue.fireAfterEvent(BeforeAfterEvent.LOSE_SANITY, "Hello");
    }

    private class DirectEventListener implements EventListener<InvestigatorId> {

        @Override
        public void onNotify(InvestigatorId eventData) {
            logger.debug("First listener notified with data '" + eventData + "'");
        }

        @Override
        public Class<InvestigatorId> getDataClass() {
            return InvestigatorId.class;
        }

        @Override
        public String toString() {
            return "" + getClass().getSimpleName();
        }
    }

    private class FirstEventListener implements EventListener<LoseTokenData> {

        @Override
        public void onNotify(LoseTokenData eventData) {
            logger.debug("First listener notified with data '" + eventData + "'");
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }

        @Override
        public String toString() {
            return "" + getClass().getSimpleName();
        }
    }

    private class SecondEventListener implements EventListener<LoseTokenData> {

        @Override
        public void onNotify(LoseTokenData eventData) {
            logger.debug("Second listener notified with data '" + eventData + "'");
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }

        @Override
        public String toString() {
            return "" + getClass().getSimpleName();
        }
    }

}
