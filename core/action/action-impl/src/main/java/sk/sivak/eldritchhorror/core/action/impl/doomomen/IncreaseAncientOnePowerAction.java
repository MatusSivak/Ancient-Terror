package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class IncreaseAncientOnePowerAction implements Action<Object, Void> {


    private final int power;

    public IncreaseAncientOnePowerAction(int power) {
        this.power = power;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().increaseAncientOnePower(power).subscribe(() -> {
                ServicePlatform.get().getModel().getAncientOne().increasePower(power);
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
