package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;

import java.util.List;

public class DestroySanityAction implements Action<Object, Void> {

    private final int sanityLost;

    public DestroySanityAction(int sanityLost) {
        this.sanityLost = sanityLost;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            int tokens = Math.min(
                    ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentSanity(),
                    sanityLost);
            ServicePlatform.get().getTestController().destroySanity(tokens).subscribe(() -> onSub.onSuccess(null));
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
