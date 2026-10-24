package sk.sivak.eldritchhorror.core.eventlistener.back.condition.righteous;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.righteous.RighteousCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.*;
import sk.sivak.eldritchhorror.core.service.*;
import static org.junit.Assert.*;

public class RighteousDaringRescueTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void selectedMonsterLosesFiveHealth() {
        ServicePlatform.nullifyInstance();
        ServicePlatform p = ServicePlatform.get();
        MonsterInfo monster = mock(MonsterInfo.class, (n, a) -> LocationId.ROME);
        List<Object[]> damage = new ArrayList<>();
        p.setService(mock(Service.class, (n, a) -> null));
        p.setInvestigators(mock(InvestigatorsRead.class, (n, a) -> mock(InvestigatorRead.class, (nn, aa) -> LocationId.ROME)));
        p.setMonsterCup(mock(MonsterCupRead.class, (n, a) -> Collections.singletonList(monster)));
        p.setMonsterService(mock(MonsterService.class, (n, a) -> {
            if (n.equals("selectSingleMonster")) return Single.just(monster);
            if (n.equals("dealDamageToMonster")) damage.add(a);
            return null;
        }));
        p.setEncounterService(mock(EncounterService.class, (n, a) ->
                n.equals("displayButtons") ? Single.just(0) : null));
        p.setGameService(mock(GameService.class, (n, a) -> Single.just(ShowCardResponse.OK)));

        new RighteousConditionBackListener4(new RighteousCondition()).execute();

        assertEquals(1, damage.size());
        assertSame(monster, damage.get(0)[0]);
        assertEquals(5, damage.get(0)[1]);
    }

    private <T> T mock(Class<T> type, BiFunction<String, Object[], Object> handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                (p, method, args) -> handler.apply(method.getName(), args)));
    }
}
