package sk.sivak.eldritchhorror.core.action.impl.monster;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class DiscardMonsterAction extends AbstractHookableAction<MonsterInfo, MonsterInfo> {

    public DiscardMonsterAction() {
        super(BeforeAfterEvent.DISCARD_MONSTER);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super MonsterInfo> ss) {
        discardMonster();
    }

    private void discardMonster() {
        ServicePlatform.get().getEventListenerProvider().unregisterMonsterListeners(input);
        ServicePlatform.get().getMonsterController().discardMonster(input).subscribe(
                () -> {
                    ServicePlatform.get().getMonsterCup().discardMonster(input);
                    ss.onSuccess(input);
                }
        );
    }
}
