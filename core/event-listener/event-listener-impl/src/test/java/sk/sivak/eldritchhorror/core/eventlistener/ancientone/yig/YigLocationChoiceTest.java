package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class YigLocationChoiceTest {
    private final List<LocationId> selected = new ArrayList<>();
    private int questions;
    private int holds;

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform.get().setService(mock(Service.class, (name, args) -> {
            if (name.equals("hold")) holds++;
            if (name.equals("release")) holds--;
            return null;
        }));
        ServicePlatform.get().setGameService(mock(GameService.class, (name, args) -> {
            assertEquals("selectLocation", name); questions++;
            SelectLocationData selection = (SelectLocationData) args[0];
            assertEquals(Arrays.asList(LocationId.TUNGUSKA, LocationId.THE_HIMALAYAS), selection.getLocations());
            return Single.just(selection.getLocations().get(1));
        }));
    }
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void oneDistinctDestinationResolvesWithoutAQuestion() {
        YigEffects.chooseLocation(Arrays.asList(LocationId.TUNGUSKA, LocationId.TUNGUSKA),
                location -> { assertEquals(1, holds); selected.add(location); });
        assertEquals(Collections.singletonList(LocationId.TUNGUSKA), selected);
        assertEquals(0, questions);
        assertEquals(0, holds);
    }
    @Test public void multipleDestinationsUseMapPickerWithoutDuplicateLocations() {
        YigEffects.chooseLocation(Arrays.asList(LocationId.TUNGUSKA, LocationId.THE_HIMALAYAS, LocationId.TUNGUSKA),
                location -> { assertEquals(1, holds); selected.add(location); });
        assertEquals(Collections.singletonList(LocationId.THE_HIMALAYAS), selected);
        assertEquals(1, questions);
        assertEquals(0, holds);
    }
    @Test public void noDestinationDoesNothing() {
        YigEffects.chooseLocation(Collections.emptyList(), selected::add);
        assertTrue(selected.isEmpty());
        assertEquals(0, questions);
    }
    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
