package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ancientone.*;
import sk.sivak.eldritchhorror.core.constants.phase.PhaseType;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class YigThirdMysteryTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void thirdMysteryQueuesAwakeningBeforeFinaleAndDoesNotDeclareVictory() throws Exception {
        cleanup(); ServicePlatform p=ServicePlatform.get();
        List<String> commands=new ArrayList<>();
        AncientOneInfo info=proxy(AncientOneInfo.class,(n,a)-> {
            if(n.equals("getAncientOneId"))return AncientOneId.YIG;
            if(n.equals("isAwaken"))return false;
            if(n.equals("getMysteriesRequired"))return 3;
            throw new AssertionError(n);
        });
        AncientOneWrite ancient=proxy(AncientOneWrite.class,(n,a)->info);
        p.setModel(proxy(ModelWrite.class,(n,a)->ancient));
        p.setMysteryDeck(proxy(MysteryDeckWrite.class,(n,a)->3));
        p.setPhaseWrite(proxy(PhaseWrite.class,(n,a)->PhaseType.ENCOUNTER));
        p.setService(proxy(Service.class,(n,a)-> {commands.add(n);return null;}));
        p.setEncounterService(proxy(EncounterService.class,(n,a)-> {commands.add(n);return null;}));
        p.setDoomOmenService(proxy(DoomOmenService.class,(n,a)-> {commands.add(n);return null;}));
        Method confirm=ResolveCurrentMysteryAction.class.getDeclaredMethod("confirmSolvedMysteriesCount");
        confirm.setAccessible(true);confirm.invoke(new ResolveCurrentMysteryAction());
        assertEquals(Arrays.asList("hold","finishTypewriterPaper","ancientOneAwakens","addEventCommand","release"),commands);
    }

    private <T>T proxy(Class<T> type,BiFunction<String,Object[],Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},
                (p,m,a)->handler.apply(m.getName(),a)));
    }
}
