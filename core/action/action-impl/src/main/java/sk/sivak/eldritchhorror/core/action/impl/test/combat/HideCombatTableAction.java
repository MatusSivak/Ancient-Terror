package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.HIDE_COMBAT_TABLE;

public class HideCombatTableAction extends AbstractHookableAction<Object, Void> {


    private final CombatData combatData;

    public HideCombatTableAction(CombatData combatData) {
        super(HIDE_COMBAT_TABLE);
        this.combatData = combatData;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        if (combatData.isCombatTableHidden()) {
            ss.onSuccess(null);
            return;
        }
        combatData.setCombatTableHidden(true);
        ServicePlatform.get().getTestController().hideCombatTable().subscribe(() -> ss.onSuccess(null));
    }

    @Override
    public void setInput(Object input) {

    }
}
