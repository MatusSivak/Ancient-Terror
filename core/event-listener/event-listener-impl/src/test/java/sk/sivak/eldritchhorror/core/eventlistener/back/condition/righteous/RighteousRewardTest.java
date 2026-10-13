package sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.righteous.RighteousCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class RighteousRewardTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void noGatesOffersDoomAndCompletesDiscard() { resolve(false, 0); }
    @Test public void gateRewardStillClosesSelectedGate() { resolve(true, 0); }
    @Test public void doomRemainsAvailableWithGates() { resolve(true, 1); }

    private void resolve(boolean hasGates, int choice) {
        ServicePlatform.nullifyInstance();
        ServicePlatform p = ServicePlatform.get();
        List<String> effects = new ArrayList<>();
        List<String[]> buttons = new ArrayList<>();
        GateInfo gate = mock(GateInfo.class, (n, a) -> LocationId.ROME);
        p.setGateStackRead(mock(GateStackRead.class, (n, a) -> hasGates
                ? Collections.singletonList(gate) : Collections.emptyList()));
        p.setService(mock(Service.class, (n, a) -> { effects.add(n); return null; }));
        p.setDoomOmenService(mock(DoomOmenService.class, (n, a) -> { effects.add(n); return null; }));
        p.setInvestigators(mock(InvestigatorsRead.class, (n, a) -> n.equals("getActiveInvestigatorId")
                ? InvestigatorId.THE_SAILOR : mock(InvestigatorRead.class, (nn, aa) -> LocationId.ROME)));
        p.setEncounterService(mock(EncounterService.class, (n, a) -> {
            effects.add(n);
            if (n.equals("displayButtons")) {
                buttons.add((String[]) a[0]);
                return Single.just(buttons.size() == 1 ? choice : 0);
            }
            return null;
        }));
        p.setGameService(mock(GameService.class, (n, a) -> {
            if (n.equals("selectSingleGate")) {
                assertTrue("Never wait for an empty gate selection", hasGates);
                return Single.just(gate);
            }
            if (n.equals("showCard")) return Single.just(ShowCardResponse.OK);
            throw new AssertionError(n);
        }));
        new RighteousConditionBackListener2(new RighteousCondition()).execute();
        assertArrayEquals(hasGates
                ? new String[]{"[#GOOD]Close one Gate[]", "[#GOOD]Retreat Doom[]"}
                : new String[]{"[#GOOD]Retreat Doom[]"}, buttons.get(0));
        assertEquals(hasGates && choice == 0, effects.contains("closeGate"));
        assertEquals(!hasGates || choice == 1, effects.contains("retreatDoom"));
        assertTrue(effects.contains("finishTypewriterPaper"));
        assertTrue(effects.contains("discardConditionFromInvestigator"));
    }

    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (p, method, args) -> handler.apply(method.getName(), args)));
    }
}
