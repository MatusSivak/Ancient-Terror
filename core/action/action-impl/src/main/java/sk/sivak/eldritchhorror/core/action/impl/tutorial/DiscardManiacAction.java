package sk.sivak.eldritchhorror.core.action.impl.tutorial;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

import java.util.List;

public class DiscardManiacAction implements Action<Object, Object> {
    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(LocationId.ISTANBUL);
            ServicePlatform.get().getMonsterService().discardMonster(monstersAtLocation.get(0));
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
