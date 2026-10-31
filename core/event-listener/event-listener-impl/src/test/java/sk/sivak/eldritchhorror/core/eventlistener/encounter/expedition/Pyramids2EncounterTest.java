package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.Test;
import rx.Single;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class Pyramids2EncounterTest {
    @Test public void everyBranchHasValidTextAndPreservesItsConsequences() {
        for (boolean firstPass : new boolean[]{false, true}) {
            for (boolean secondPass : new boolean[]{false, true}) {
                ServicePlatform.nullifyInstance();
                try {
                    ServicePlatform platform = ServicePlatform.get();
                    List<String> text = new ArrayList<>(), effects = new ArrayList<>();
                    int[] tests = {0};
                    platform.setService(mock(Service.class, (name, args) -> null));
                    platform.setEncounterService(mock(EncounterService.class, (name, args) -> {
                        if (name.equals("typeFlavor") || name.equals("typeInfo")) {
                            assertNotNull("Missing " + name, args[0]);
                            assertFalse(((String) args[0]).contains("null"));
                            text.add((String) args[0]);
                        }
                        return name.equals("displayButtons") ? Single.just(0) : null;
                    }));
                    platform.setTestService(mock(TestService.class, (name, args) -> {
                        boolean pass = tests[0]++ == 0 ? firstPass : secondPass;
                        return Single.just(new TestData() {
                            @Override public boolean isSuccessful() { return pass; }
                        });
                    }));
                    platform.setTokenService(mock(TokenService.class, (name, args) -> {
                        effects.add(name + (args == null ? "" : ":" + args[0])); return null;
                    }));
                    platform.setGameService(mock(GameService.class, (name, args) -> {
                        effects.add(name + (args == null ? "" : ":" + args[0])); return null;
                    }));
                    new Pyramids2Encounter().executeWhole();
                    assertEquals(2, tests[0]);
                    assertFalse(text.isEmpty());
                    if (!firstPass && !secondPass) {
                        assertTrue(effects.contains("loseHealth:1"));
                        assertTrue(effects.stream().anyMatch(s -> s.startsWith("gainCondition:")));
                        assertTrue(text.stream().anyMatch(s -> s.contains("Internal Injury")));
                    } else if (!firstPass) assertEquals(2, Collections.frequency(effects, "gainClueFromPool"));
                    else if (secondPass) assertTrue(effects.contains("gainArtifact"));
                    else assertTrue(effects.contains("loseSanity:2"));
                } finally { ServicePlatform.nullifyInstance(); }
            }
        }
    }

    private static <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
