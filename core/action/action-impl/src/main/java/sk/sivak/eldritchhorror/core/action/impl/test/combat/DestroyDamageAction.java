package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;

import java.util.List;

public class DestroyDamageAction implements Action<Object, Void> {

    private final List<DiceRoll> diceRolls;

    public DestroyDamageAction(List<DiceRoll> diceRolls) {
        this.diceRolls = diceRolls;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getTestController().destroyDamage(diceRolls).subscribe(() -> onSub.onSuccess(null));
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
