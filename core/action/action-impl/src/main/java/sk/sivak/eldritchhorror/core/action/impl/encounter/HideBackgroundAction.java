package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

public class HideBackgroundAction implements Action<Object, Void> {

    public HideBackgroundAction() {
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            ServicePlatform.get().getModel().getBackgroundModel().popBackground(activeInvestigatorId);
            ServicePlatform.get().getGameController().hideBackground().subscribe(() -> {
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
