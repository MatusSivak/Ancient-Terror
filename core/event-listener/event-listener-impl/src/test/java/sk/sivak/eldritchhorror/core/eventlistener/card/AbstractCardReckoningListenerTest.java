package sk.sivak.eldritchhorror.core.eventlistener.card;

import java8.features.function.Consumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.service.DoomOmenService;
import sk.sivak.eldritchhorror.core.service.GameService;
import sk.sivak.eldritchhorror.core.service.InvestigatorService;
import sk.sivak.eldritchhorror.core.service.Service;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import sk.sivak.eldritchhorror.core.constants.asset.ConspiracyTheoristAsset;
import sk.sivak.eldritchhorror.core.eventlistener.asset.ConspiracyTheoristListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import static org.junit.Assert.assertEquals;

public class AbstractCardReckoningListenerTest {

    private final InvestigatorId owner = InvestigatorId.THE_SPY;
    private final Set<InvestigatorId> lost = new HashSet<>();
    private int triggered;
    private boolean defer;
    private final List<Consumer<Object>> pending = new ArrayList<>();

    private final AbstractCardReckoningListener listener = new AbstractCardReckoningListener() {
        @Override
        protected InvestigatorId getCardOwnerId() {
            return owner;
        }

        @Override
        protected void onNotify() {
            triggered++;
        }
    };

    @Before
    public void setUp() {
        ServicePlatform.nullifyInstance();
        ServicePlatform.get().setInvestigators((InvestigatorsRead) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[]{InvestigatorsRead.class}, (proxy, method, args) -> {
                    if (method.getName().equals("isOnBoard")) return !lost.contains(args[0]);
                    if (method.getName().equals("getInvestigator")) return Proxy.newProxyInstance(
                            getClass().getClassLoader(), new Class[]{InvestigatorRead.class},
                            (p, m, a) -> false);
                    throw new AssertionError(method.getName());
                }));
        ServicePlatform.get().setService((Service) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{Service.class}, (proxy, method, args) -> {
                    if (method.getName().equals("addEventCommand")) {
                        if (defer) pending.add((Consumer<Object>) args[0]);
                        else ((Consumer<Object>) args[0]).accept(null);
                    }
                    return null;
                }));
        ServicePlatform.get().setDoomOmenService(noop(DoomOmenService.class));
        ServicePlatform.get().setInvestigatorService(noop(InvestigatorService.class));
        ServicePlatform.get().setGameService(noop(GameService.class));
    }

    @After
    public void tearDown() {
        ServicePlatform.nullifyInstance();
    }

    @Test
    public void reckoningResolvesForInvestigatorOnBoard() {
        reckon();
        assertEquals(1, triggered);
    }

    @Test
    public void reckoningIsSkippedWhileOwnerIsLostInTimeAndSpace() {
        lost.add(owner);
        reckon();
        assertEquals(0, triggered);

        lost.remove(owner);
        reckon();
        assertEquals(1, triggered);
    }

    private void reckon() {
        listener.onNotify(ReckoningFireType.CHARGE);
        listener.onNotify(ReckoningFireType.FIRE);
    }

    @Test
    public void discardedConspiracyTheoristDoesNotResolveAlreadyQueuedReckoning() {
        EventQueueImpl events = new EventQueueImpl();
        ServicePlatform.get().setEventQueue(events);
        ServicePlatform.get().setGameService((GameService) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{GameService.class}, (p, m, a) -> {
                    if (m.getName().equals("showCard")) { triggered++; return rx.Single.create(s -> {}); }
                    return null;
                }));
        ConspiracyTheoristListener ally = new ConspiracyTheoristListener();
        ally.registerWithOwner(owner, new ConspiracyTheoristAsset());
        defer = true;
        events.fireDirectEvent(DirectEvent.RECKONING_CARDS_2, ReckoningFireType.CHARGE);
        events.fireDirectEvent(DirectEvent.RECKONING_CARDS_2, ReckoningFireType.FIRE);
        assertEquals(1, pending.size());
        // Serpent Crown's earlier queued effect discards this Ally before its effect runs.
        ally.unregister();
        pending.remove(0).accept(null);
        assertEquals(0, triggered);

        ally.registerWithOwner(owner, new ConspiracyTheoristAsset());
        events.fireDirectEvent(DirectEvent.RECKONING_CARDS_2, ReckoningFireType.CHARGE);
        events.fireDirectEvent(DirectEvent.RECKONING_CARDS_2, ReckoningFireType.FIRE);
        pending.remove(0).accept(null);
        assertEquals(1, triggered);
    }

    @Test
    public void reregisteringCannotReviveOldQueuedReckoningButAllowsTheNextOne() {
        EventQueueImpl events = new EventQueueImpl();
        InvestigatorCardEventQueue cards = new InvestigatorCardEventQueue(events, () -> owner);
        cards.addDirectEventListener(listener, DirectEvent.RECKONING_CARDS_2);
        defer = true;
        reckon();
        cards.unregisterListener(listener);
        cards.addDirectEventListener(listener, DirectEvent.RECKONING_CARDS_2);
        reckon();
        pending.remove(0).accept(null);
        assertEquals(0, triggered);
        pending.remove(0).accept(null);
        assertEquals(1, triggered);
    }

    @Test
    public void unregistrationAlsoClearsChargeBeforeFire() {
        InvestigatorCardEventQueue cards = new InvestigatorCardEventQueue(new EventQueueImpl(), () -> owner);
        cards.addDirectEventListener(listener, DirectEvent.RECKONING_CARDS_2);
        listener.onNotify(ReckoningFireType.CHARGE);
        cards.unregisterListener(listener);
        cards.addDirectEventListener(listener, DirectEvent.RECKONING_CARDS_2);
        listener.onNotify(ReckoningFireType.FIRE);
        assertEquals(0, triggered);
        reckon();
        assertEquals(1, triggered);
    }

    @SuppressWarnings("unchecked")
    private static <T> T noop(Class<T> type) {
        return (T) Proxy.newProxyInstance(AbstractCardReckoningListenerTest.class.getClassLoader(),
                new Class[]{type}, (proxy, method, args) -> null);
    }
}
