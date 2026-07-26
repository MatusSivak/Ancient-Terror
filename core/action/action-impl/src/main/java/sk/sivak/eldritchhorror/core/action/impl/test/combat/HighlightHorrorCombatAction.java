package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class HighlightHorrorCombatAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getTestController()
                    .highlightHorrorCombat()
                    .subscribe(() -> onSub.onSuccess(null));
        });
    }

    @Override
    public void setInput(Object input) {
    }
}
