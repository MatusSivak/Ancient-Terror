package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class EndOfCombatEventAction extends AbstractHookableAction<Object, CombatData> {

    private final CombatData combatData;

    public EndOfCombatEventAction(CombatData combatData) {
        super(BeforeAfterEvent.END_OF_COMBAT_EVENT);
        this.combatData = combatData;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CombatData> ss) {
        ss.onSuccess(combatData);
    }
}
