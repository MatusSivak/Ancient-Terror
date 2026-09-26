package sk.sivak.eldritchhorror.core.eventlistener.card;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.condition.lostintimeandspace.LostInTimeAndSpaceCondition;
import sk.sivak.eldritchhorror.core.constants.spell.fleshward.FleshWardSpell;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.condition.LostInTimeAndSpaceListener;
import sk.sivak.eldritchhorror.core.eventlistener.spell.FleshWardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.service.Service;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class InvestigatorCardEventQueueTest {
    private final EventQueueImpl events = new EventQueueImpl();
    private final Set<InvestigatorId> lost = new HashSet<>();
    private InvestigatorId owner = InvestigatorId.THE_SPY;
    private int calls;
    private int queuedCommands;
    private InvestigatorCardEventQueue cards;

    @Before public void setUp() {
        ServicePlatform.nullifyInstance();
        ServicePlatform.get().setEventQueue(events);
        ServicePlatform.get().setInvestigators((InvestigatorsRead) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[]{InvestigatorsRead.class}, (proxy, method, args) -> {
                    if (method.getName().equals("getActiveInvestigatorId")) return owner;
                    if (method.getName().equals("getInvestigator")) {
                        InvestigatorId id = (InvestigatorId) args[0];
                        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{InvestigatorRead.class},
                                (p, m, a) -> {
                                    if (m.getName().equals("isLostInTimeAndSpace")) return lost.contains(id);
                                    throw new AssertionError(m.getName());
                                });
                    }
                    throw new AssertionError(method.getName());
                }));
        ServicePlatform.get().setService((Service) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{Service.class}, (proxy, method, args) -> {
                    if (method.getName().equals("addEventCommand")) { queuedCommands++; return null; }
                    throw new AssertionError(method.getName());
                }));
        cards = new InvestigatorCardEventQueue(events, () -> owner);
    }

    @After public void tearDown() { ServicePlatform.nullifyInstance(); }

    private <T> EventListener<T> listener(Class<T> type) {
        return new EventListener<T>() {
            public void onNotify(T data) { calls++; }
            public Class<T> getDataClass() { return type; }
        };
    }

    @Test public void suspendsBeforeAfterAndDirectReactionsAndResumesOnReturn() {
        EventListener<LoseTokenData> health = listener(LoseTokenData.class);
        cards.addBeforeEventListener(health, BeforeAfterEvent.LOSE_HEALTH);
        cards.addAfterEventListener(health, BeforeAfterEvent.LOSE_HEALTH);
        cards.addDirectEventListener(listener(Object.class), DirectEvent.PERFORM_ACTION_START);
        fireAll();
        assertEquals(3, calls);
        lost.add(owner);
        fireAll();
        assertEquals(3, calls);
        lost.remove(owner);
        fireAll();
        assertEquals(6, calls);
    }

    private void fireAll() {
        events.fireBeforeEvent(BeforeAfterEvent.LOSE_HEALTH, null);
        events.fireAfterEvent(BeforeAfterEvent.LOSE_HEALTH, null);
        events.fireDirectEvent(DirectEvent.PERFORM_ACTION_START, null);
    }

    @Test public void followsOwnerChangesAndUnregistersEveryRegistration() {
        EventListener<LoseTokenData> listener = listener(LoseTokenData.class);
        cards.addBeforeEventListener(listener, BeforeAfterEvent.LOSE_HEALTH);
        cards.addAfterEventListener(listener, BeforeAfterEvent.LOSE_HEALTH);
        lost.add(owner);
        owner = InvestigatorId.THE_SAILOR;
        fireAll();
        assertEquals(2, calls);
        cards.unregisterListener(listener);
        fireAll();
        assertEquals(2, calls);
    }

    @Test public void refreshStillRunsWhileLostAndCanBeUnregistered() {
        EventListener<Void> refresh = listener(Void.class);
        cards.addDirectEventListener(refresh, DirectEvent.REENABLE_DISABLED_ABILITIES);
        lost.add(owner);
        events.fireDirectEvent(DirectEvent.REENABLE_DISABLED_ABILITIES, null);
        assertEquals(1, calls);
        cards.unregisterListener(refresh);
        events.fireDirectEvent(DirectEvent.REENABLE_DISABLED_ABILITIES, null);
        assertEquals(1, calls);
    }

    @Test public void fleshWardDoesNotQueueAnOfferWhileItsOwnerIsLost() {
        FleshWardListener fleshWard = new FleshWardListener();
        fleshWard.register(new FleshWardSpell(), owner);
        lost.add(owner);
        events.fireBeforeEvent(BeforeAfterEvent.LOSE_HEALTH, new LoseTokenData(1, sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType.HEALTH));
        assertEquals(0, queuedCommands);
        lost.remove(owner);
        events.fireBeforeEvent(BeforeAfterEvent.LOSE_HEALTH, new LoseTokenData(1, sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType.HEALTH));
        assertEquals(1, queuedCommands);
        fleshWard.unregister();
        events.fireBeforeEvent(BeforeAfterEvent.LOSE_HEALTH, new LoseTokenData(1, sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType.HEALTH));
        assertEquals(1, queuedCommands);
    }

    @Test public void lostConditionItselfStillOffersItsReturnEncounter() {
        LostInTimeAndSpaceListener condition = new LostInTimeAndSpaceListener();
        condition.register(new LostInTimeAndSpaceCondition(), owner);
        lost.add(owner);
        AvailableEncounters available = new AvailableEncounters();
        events.fireDirectEvent(DirectEvent.COLLECT_COMMON_ENCOUNTERS, available);
        assertEquals(1, available.getEncounters().size());
        lost.remove(owner);
        events.fireBeforeEvent(BeforeAfterEvent.SHOW_ACTIVE_INVESTIGATOR, owner);
        available = new AvailableEncounters();
        events.fireDirectEvent(DirectEvent.COLLECT_COMMON_ENCOUNTERS, available);
        assertTrue("Returning investigator must not repeat the lost encounter before discard", available.getEncounters().isEmpty());
        condition.unregister();
        available = new AvailableEncounters();
        events.fireDirectEvent(DirectEvent.COLLECT_COMMON_ENCOUNTERS, available);
        assertTrue(available.getEncounters().isEmpty());
    }
}
