package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.gate.*;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.SelectSingleGateData;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class GreenMythosCard10Test {
    private final List<String> effects = new ArrayList<>();
    private List<GateInfo> gates;
    private GateColor selectedColor = GateColor.RED;
    private SelectSingleGateData selection;

    @Before public void setup() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        GateInfo gate = mock(GateInfo.class, (name, args) ->
                name.equals("getLocationId") ? LocationId.ARKHAM : selectedColor);
        gates = Collections.singletonList(gate);
        platform.setGateStackRead(mock(GateStackRead.class, (name, args) -> gates));
        OmenInfo omen = mock(OmenInfo.class, (name, args) -> OmenColor.RED);
        platform.setOmenTrack(mock(OmenTrackRead.class, (name, args) -> omen));
        platform.setGameService(mock(GameService.class, (name, args) -> {
            assertEquals("selectSingleGate", name);
            selection = (SelectSingleGateData) args[0];
            return Single.just(selection.getGates().get(0));
        }));
        platform.setService(mock(Service.class, (name, args) -> {
            effects.add(name);
            if (name.equals("discardGate")) assertEquals(LocationId.ARKHAM, args[0]);
            return null;
        }));
        platform.setDoomOmenService(mock(DoomOmenService.class, (name, args) -> { effects.add(name); return null; }));
    }
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void choiceExplainsGateDiscardAndItsDoomConsequence() {
        new GreenMythosCard10().execute();
        assertTrue(selection.getTitleText().contains("Choose a Gate to discard"));
        assertTrue(selection.getTitleText().contains("advance Doom by 1"));
        assertEquals(Arrays.asList("hold", "discardGate", "release"), effects);
    }
    @Test public void mismatchedColorStillAdvancesDoom() {
        selectedColor = GateColor.BLUE;
        new GreenMythosCard10().execute();
        assertEquals(Arrays.asList("hold", "discardGate", "advanceDoom", "release"), effects);
    }
    @Test public void noGatesCannotOpenAnUnanswerableChooser() {
        gates = Collections.emptyList();
        new GreenMythosCard10().execute();
        assertNull(selection);
        assertTrue(effects.isEmpty());
    }
    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
