package sk.sivak.eldritchhorror.core.action.impl.encounter.execute;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.After;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueWrite;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.service.EncounterService;
import sk.sivak.eldritchhorror.core.service.Service;

import static org.junit.Assert.*;

public class MysteryEncounterExecutorTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void closesChooserBeforeMysteryContentAndPreservesEncounterCompletion() {
        ServicePlatform.nullifyInstance();
        ServicePlatform platform = ServicePlatform.get();
        MysteryEncounter encounter = new MysteryEncounter("K'n-yan Unearthed");
        List<Runnable> commands = new ArrayList<>();
        List<String> displayed = new ArrayList<>();
        boolean[] chooserVisible = {true};

        platform.setService(mock(Service.class, (name, args) -> {
            if (name.equals("hideSelectEncounterTable")) commands.add(() -> {
                chooserVisible[0] = false;
                displayed.add("chooser closed");
            });
            else assertTrue(name, name.equals("hold") || name.equals("release"));
            return null;
        }));
        platform.setEventQueue(mock(EventQueueWrite.class, (name, args) -> {
            assertEquals("fireDirectEvent", name);
            assertEquals(DirectEvent.ENCOUNTER_ACTIVE_MYSTERY, args[0]);
            assertSame(encounter, args[1]);
            // A mystery listener queues its background, narrative, and tests here.
            commands.add(() -> {
                assertFalse("The chooser must not cover mystery content", chooserVisible[0]);
                displayed.add("mystery content");
            });
            return null;
        }));
        platform.setEncounterService(mock(EncounterService.class, (name, args) -> {
            if (name.equals("endOfEncounter")) assertSame(encounter, args[0]);
            commands.add(() -> displayed.add(name));
            return null;
        }));

        MysteryEncounterExecutor.execute(encounter);
        for (Runnable command : commands) command.run();

        assertEquals(Arrays.asList("chooser closed", "mystery content", "endOfEncounter",
                "insertDefeatSequencePoint"), displayed);
    }

    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (proxy, method, args) -> handler.apply(method.getName(), args)));
    }
}
