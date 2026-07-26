package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.AFTER_HORROR_CHECK;

public class AfterHorrorCheckAction extends AbstractHookableAction<CombatData, CombatData> {


    public AfterHorrorCheckAction() {
        super(AFTER_HORROR_CHECK);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CombatData> ss) {
        ServicePlatform.get().getTestController().waitForCombatTableCentered().subscribe(() -> {
            ss.onSuccess(input);
        });
    }
}
