package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class ShowCombatOverviewAction extends AbstractHookableAction<CombatData, CombatData> {


    public ShowCombatOverviewAction() {
        super(BeforeAfterEvent.SHOW_COMBAT_OVERVIEW);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CombatData> ss) {
        ServicePlatform.get().getTestController().showCombatOverview(
                ServicePlatform.get().getInvestigators().getActiveInvestigatorId(), input.getMonsterInfo(), input.getDamageTestType()).subscribe(() -> {
            ss.onSuccess(input);
        });
    }
}
