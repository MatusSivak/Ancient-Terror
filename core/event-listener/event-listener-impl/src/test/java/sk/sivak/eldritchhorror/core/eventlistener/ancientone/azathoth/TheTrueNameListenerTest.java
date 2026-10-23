package sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsWrite;
import sk.sivak.eldritchhorror.core.service.GameService;
import sk.sivak.eldritchhorror.core.service.Service;
import sk.sivak.eldritchhorror.core.service.TokenService;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

public class TheTrueNameListenerTest {
    private final List<String> effects = new ArrayList<>();
    private EventQueueImpl events;

    @Before
    public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        events = new EventQueueImpl();
        platform.setEventQueue(events);
        platform.setService(mock(Service.class, (name, a) -> null));
        platform.setGameService(mock(GameService.class, (name, a) -> {
            if (name.equals("ask")) return Single.just(new Answer<Object, Object>());
            effects.add(name + (a == null ? "" : ":" + Arrays.toString(a)));
            return null;
        }));
        platform.setTokenService(mock(TokenService.class, (name, a) -> {
            SpendData spendData = new SpendData();
            spendData.setHasEnough(true);
            spendData.setPayAction(() -> effects.add("pay"));
            return Single.just(spendData);
        }));
        InvestigatorRead investigator = mock(InvestigatorRead.class, (name, a) -> LocationId.TOKYO);
        platform.setInvestigators(mock(InvestigatorsWrite.class, (name, a) -> investigator));
    }

    @After
    public void tearDown() {
        ServicePlatform.nullifyInstance();
    }

    @Test
    public void spendingCluesMovesThatTokenToTheCard() {
        List<LocationId> pins = new LinkedList<>(Arrays.asList(LocationId.TOKYO, LocationId.ROME));
        MysteryCardInfo card = mock(MysteryCardInfo.class, (name, a) -> {
            if (name.equals("getMysteryComplexity")) return 2;
            if (name.equals("getPinLocations")) return pins;
            return null;
        });
        TheTrueNameListener listener = new TheTrueNameListener(card);
        listener.justRegisterListeners(0);

        events.fireDirectEvent(DirectEvent.ENCOUNTER_ACTIVE_MYSTERY, new MysteryEncounter(card.getName()));

        assertEquals(1, listener.getProgress());
        assertEquals(Arrays.asList(LocationId.ROME), pins);
        assertEquals(Arrays.asList("pay", "advanceCurrentMysteryCard:[1]", "clearRedPin:[" + LocationId.TOKYO + "]"), effects);
        listener.unregister();
    }

    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (p, m, a) -> handler.apply(m.getName(), a)));
    }
}
