package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import java.util.List;

public class DestroyMonsterHealthAction extends AbstractHookableAction<CombatData, CombatData> {

    private final List<DiceRoll> diceRolls;

    public DestroyMonsterHealthAction(List<DiceRoll> diceRolls) {
        super(BeforeAfterEvent.DESTROY_MONSTER_HEALTH);
        this.diceRolls = diceRolls;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CombatData> ss) {
        ServicePlatform.get().getTestController().destroyMonsterHealth(diceRolls).subscribe(() -> ss.onSuccess(input));
    }
}
