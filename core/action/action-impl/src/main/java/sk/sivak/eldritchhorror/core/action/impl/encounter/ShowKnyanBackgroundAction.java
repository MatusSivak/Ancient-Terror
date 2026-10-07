package sk.sivak.eldritchhorror.core.action.impl.encounter;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

public class ShowKnyanBackgroundAction implements Action<Object, Object> {
    private Object input;

    @Override
    public Single<Object> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().showCustomBackground("knyan").subscribe(() -> {
                InvestigatorId investigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                BackgroundData background = new BackgroundData(BackgroundModelRead.BackgroundType.CUSTOM,
                        "knyan", "encounter/background/knyan.jpg");
                ServicePlatform.get().getModel().getBackgroundModel().pushBackground(investigatorId, background);
                onSub.onSuccess(input);
            });
        });
    }

    @Override
    public void setInput(Object input) { this.input = input; }
}
