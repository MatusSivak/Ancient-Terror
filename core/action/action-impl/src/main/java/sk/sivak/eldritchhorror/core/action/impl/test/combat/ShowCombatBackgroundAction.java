package sk.sivak.eldritchhorror.core.action.impl.test.combat;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.model.BackgroundData;
import sk.sivak.eldritchhorror.core.model.BackgroundModelRead;

public class ShowCombatBackgroundAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getGameController().showCombatBackground().subscribe(() -> {
                InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                BackgroundData backgroundData = new BackgroundData(BackgroundModelRead.BackgroundType.COMBAT, null, "encounter/background/combat.jpg");
                ServicePlatform.get().getModel().getBackgroundModel().pushBackground(activeInvestigatorId, backgroundData);
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
