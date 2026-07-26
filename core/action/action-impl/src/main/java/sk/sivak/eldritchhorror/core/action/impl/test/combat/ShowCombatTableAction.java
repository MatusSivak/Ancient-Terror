package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.HIDE_COMBAT_TABLE;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.SHOW_COMBAT_TABLE;

public class ShowCombatTableAction extends AbstractHookableAction<CombatData, CombatData> {


    public ShowCombatTableAction() {
        super(SHOW_COMBAT_TABLE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CombatData> ss) {
        // createMonsterCombatTableData(monsterInfo, actualHorror, actualDamage);
        ServicePlatform.get().getTestController().showCombatTable(
                input.getMonsterInfo(), input.getActualHorror(), input.getActualDamage())
                .subscribe(() -> ss.onSuccess(input));
    }
}
