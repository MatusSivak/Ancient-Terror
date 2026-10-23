package sk.sivak.eldritchhorror.core.action.impl.monster;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.BiFunction;
import org.junit.*;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.*;
import sk.sivak.eldritchhorror.core.controller.*;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.MoveMonsterData;
import sk.sivak.eldritchhorror.core.model.MonsterCupWrite;
import static org.junit.Assert.*;

public class MoveMonsterGroupTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void groupUsesOneCameraAndCommitsEveryLocationAfterAnimation() {
        for(int count:new int[]{1,3}) {
            cleanup();
            List<MonsterInfo> monsters=new ArrayList<>();
            for(int i=0;i<count;i++) {
                CultistMonster monster=new CultistMonster();monster.setCurrentLocation(LocationId.ROME);monsters.add(monster);
            }
            PublishSubject<Void> camera=PublishSubject.create(), animation=PublishSubject.create();
            List<String> effects=new ArrayList<>();
            ServicePlatform platform=ServicePlatform.get();
            platform.setGameController(proxy(GameController.class,(name,a)-> {
                assertEquals("moveCameraToLocation",name);assertEquals(LocationId.ROME,a[0]);
                return camera.toCompletable().doOnSubscribe(subscription -> effects.add("camera"));
            }));
            platform.setMonsterController(proxy(MonsterController.class,(name,a)-> {
                assertEquals(count==1?"moveMonster":"moveMonsters",name);
                assertEquals(count==1?monsters.get(0):monsters,a[0]);assertEquals(LocationId.SPACE_7,a[1]);
                return animation.toCompletable().doOnSubscribe(subscription -> effects.add("animate"));
            }));
            platform.setMonsterCup(proxy(MonsterCupWrite.class,(name,a)-> {
                assertEquals("updateMonsterLocation",name);
                ((AbstractMonsterInfo)a[0]).setCurrentLocation((LocationId)a[1]);effects.add("update");return null;
            }));
            MoveMonsterData data=new MoveMonsterData(monsters,LocationId.SPACE_7);
            MoveMonsterAction action=new MoveMonsterAction();action.setInput(data);
            action.execute().subscribe(result -> { assertSame(data,result);effects.add("done"); });
            assertEquals(Collections.singletonList("camera"),effects);
            camera.onCompleted();assertEquals(Arrays.asList("camera","animate"),effects);
            for(MonsterInfo monster:monsters)assertEquals(LocationId.ROME,monster.getCurrentLocation());
            animation.onCompleted();
            assertEquals(count,Collections.frequency(effects,"update"));
            assertEquals(1,Collections.frequency(effects,"done"));
            for(MonsterInfo monster:monsters)assertEquals(LocationId.SPACE_7,monster.getCurrentLocation());
        }
    }

    @Test(expected=IllegalArgumentException.class) public void groupRejectsMixedOrigins() {
        CultistMonster a=new CultistMonster(), b=new CultistMonster();
        a.setCurrentLocation(LocationId.ROME);b.setCurrentLocation(LocationId.TOKYO);
        new MoveMonsterData(Arrays.asList(a,b),LocationId.SPACE_7);
    }

    private <T>T proxy(Class<T> type,BiFunction<String,Object[],Object> action) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},
                (p,m,a)->action.apply(m.getName(),a)));
    }
}
