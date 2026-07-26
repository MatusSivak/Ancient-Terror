package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

public class DefeatMonsterAction extends AbstractHookableAction<DefeatMonsterData, DefeatMonsterData> {

    public DefeatMonsterAction() {
        super(BeforeAfterEvent.DEFEAT_MONSTER);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super DefeatMonsterData> ss) {
        if (input.isDestroyCard()) {
            ServicePlatform.get().getMonsterController().tearMonsterCardApart(input.getMonsterInfo()).subscribe(this::defeatMonster);
        } else {
            defeatMonster();
        }
    }

    private void defeatMonster() {
        ServicePlatform.get().getEventListenerProvider().unregisterMonsterListeners(input.getMonsterInfo());
        ServicePlatform.get().getMonsterCup().defeatMonster(input.getMonsterInfo());
        ServicePlatform.get().getMonsterController().defeatMonster(input.getMonsterInfo()).subscribe(
                () -> ss.onSuccess(input)
        );
    }
}
