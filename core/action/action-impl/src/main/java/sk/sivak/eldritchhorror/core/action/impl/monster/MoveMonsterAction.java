package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.Completable;
import rx.Observable;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.MoveMonsterData;

public class MoveMonsterAction extends AbstractHookableAction<MoveMonsterData, MoveMonsterData> {

    public MoveMonsterAction() {
        super(BeforeAfterEvent.MOVE_MONSTER);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super MoveMonsterData> ss) {
        Completable moveCameraToLocation = ServicePlatform.get().getGameController().moveCameraToLocation(input.getMonsterInfo().getCurrentLocation());
        Completable moveMonster = ServicePlatform.get().getMonsterController().moveMonster(input.getMonsterInfo(), input.getTargetLocationId());

        moveCameraToLocation.subscribe(() -> moveMonster.subscribe(() -> {
            ServicePlatform.get().getMonsterCup().updateMonsterLocation(input.getMonsterInfo(), input.getTargetLocationId());
            ss.onSuccess(input);
        }));
    }
}
