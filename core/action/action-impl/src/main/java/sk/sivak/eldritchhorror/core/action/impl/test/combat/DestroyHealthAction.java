package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class DestroyHealthAction extends AbstractHookableAction<CombatData, CombatData> {

    public DestroyHealthAction() {
        super(BeforeAfterEvent.DESTROY_INVESTIGATOR_HEALTH);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CombatData> ss) {
        int tokens = Math.min(
                ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentHealth(),
                input.getHealthLost());
        ServicePlatform.get().getTestController().destroyHealth(tokens).subscribe(() -> ss.onSuccess(input));
    }
}
