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

import static org.junit.Assert.assertEquals;

public class AbstractCardReckoningListenerTest {

    private final InvestigatorId owner = InvestigatorId.THE_SPY;
    private final Set<InvestigatorId> lost = new HashSet<>();
    private int triggered;

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
                    throw new AssertionError(method.getName());
                }));
        ServicePlatform.get().setService((Service) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{Service.class}, (proxy, method, args) -> {
                    if (method.getName().equals("addEventCommand")) {
                        ((Consumer<Object>) args[0]).accept(null);
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

    @SuppressWarnings("unchecked")
    private static <T> T noop(Class<T> type) {
        return (T) Proxy.newProxyInstance(AbstractCardReckoningListenerTest.class.getClassLoader(),
                new Class[]{type}, (proxy, method, args) -> null);
    }
}
