package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.Completable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.MoveMonsterData;

public class MoveMonsterAction extends AbstractHookableAction<MoveMonsterData, MoveMonsterData> {

    public MoveMonsterAction() {
        super(BeforeAfterEvent.MOVE_MONSTER);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super MoveMonsterData> ss) {
        Completable moveCameraToLocation = ServicePlatform.get().getGameController().moveCameraToLocation(input.getMonsterInfo().getCurrentLocation());
        Completable moveMonster = input.getMonsters().size()==1
                ? ServicePlatform.get().getMonsterController().moveMonster(input.getMonsterInfo(), input.getTargetLocationId())
                : ServicePlatform.get().getMonsterController().moveMonsters(input.getMonsters(), input.getTargetLocationId());

        moveCameraToLocation.andThen(moveMonster).subscribe(() -> {
            for(MonsterInfo monster:input.getMonsters())
                ServicePlatform.get().getMonsterCup().updateMonsterLocation(monster, input.getTargetLocationId());
            ss.onSuccess(input);
        }, ss::onError);
    }
}
