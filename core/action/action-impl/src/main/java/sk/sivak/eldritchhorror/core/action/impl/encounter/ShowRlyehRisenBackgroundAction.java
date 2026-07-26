package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

public class ShowRlyehRisenBackgroundAction implements Action<Object, Object> {

    private Object input;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().showCustomBackground("rlyeh").subscribe(() -> {
                InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                BackgroundData backgroundData = new BackgroundData(BackgroundModelRead.BackgroundType.CUSTOM, "rlyeh", "encounter/background/rlyeh.jpg");
                ServicePlatform.get().getModel().getBackgroundModel().pushBackground(activeInvestigatorId, backgroundData);
                onSub.onSuccess(input);
            });
        });
    }

    @Override
    public void setInput(Object input) {
        this.input = input;
    }
}
