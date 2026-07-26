package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

/**
 * @author msivak
 */
public class UpdateMonsterHorrorAction extends AbstractHookableAction<CombatData, CombatData> {

    public UpdateMonsterHorrorAction() {
        super(BeforeAfterEvent.UPDATE_MONSTER_HORROR);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super CombatData> ss) {
        ServicePlatform.get().getTestController().updateMonsterHorror(input.getActualHorror()).subscribe(() -> {
            ss.onSuccess(input);
        });
    }
}
