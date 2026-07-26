package sk.sivak.eldritchhorror.core.action.provider;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class ReturnAmbushingMonsterAction implements Action<Object, MonsterInfo> {


    private final MonsterInfo monsterInfo;

    public ReturnAmbushingMonsterAction(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    @Override
    public Single<MonsterInfo> execute() {
        ServicePlatform.get().getEventListenerProvider().unregisterMonsterListeners(monsterInfo);
        ServicePlatform.get().getMonsterCup().returnAmbushingMonster(monsterInfo);
        return Single.just(monsterInfo);
    }

    @Override
    public void setInput(Object input) {

    }
}
