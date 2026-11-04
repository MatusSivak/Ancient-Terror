package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class DestroyMonsterHealthAction extends AbstractHookableAction<CombatData, CombatData> {

    public DestroyMonsterHealthAction() {
        super(BeforeAfterEvent.DESTROY_MONSTER_HEALTH);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CombatData> ss) {
        ServicePlatform.get().getTestController().destroyMonsterHealth(input.getDamageTestResult().getScore()).subscribe(() -> ss.onSuccess(input));
    }
}
