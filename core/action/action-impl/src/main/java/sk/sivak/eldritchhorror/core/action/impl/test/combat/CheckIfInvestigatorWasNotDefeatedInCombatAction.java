package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.InvestigatorWrite;

public class CheckIfInvestigatorWasNotDefeatedInCombatAction implements Action<Object, Object> {

    private Object input;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            InvestigatorWrite activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
            if (activeInvestigator == null) {
                onSub.onSuccess(input);
                return;
            }
            if (activeInvestigator.getCurrentHealth() <= 0 || activeInvestigator.getCurrentSanity() <= 0) {
                ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.END_OF_ENCOUNTER, null);
            }
            onSub.onSuccess(input);
        });
    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
