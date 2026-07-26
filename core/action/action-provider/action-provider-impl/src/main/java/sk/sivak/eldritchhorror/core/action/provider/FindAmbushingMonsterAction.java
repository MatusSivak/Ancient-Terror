package sk.sivak.eldritchhorror.core.action.provider;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class FindAmbushingMonsterAction implements Action<Object, MonsterInfo> {

    private final MonsterId monsterId;

    public FindAmbushingMonsterAction(MonsterId monsterId) {
        this.monsterId = monsterId;
    }

    @Override
    public Single<MonsterInfo> execute() {
        return Single.create(onSub -> {
            LocationId locationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
            MonsterInfo ambushingMonster = ServicePlatform.get().getMonsterCup().initAmbushingMonster(monsterId, locationId);
            ServicePlatform.get().getEventListenerProvider().justRegisterMonsterListeners(ambushingMonster);
            onSub.onSuccess(ambushingMonster);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
