package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.RumorCardInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.rumor.InitRumorCardData;

public class ShowRumorCardAction implements Action<Object, Void> {

    private final String rumorId;

    public ShowRumorCardAction(String rumorId) {
        this.rumorId = rumorId;
    }

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            RumorCardInfo activeRumor = ServicePlatform.get().getRumors().getActiveRumor(rumorId);
            ServicePlatform.get().getGameController().showRumorCard(activeRumor).subscribe(() -> {
                ServicePlatform.get().getGameController().updateRumorButtonVisibility();
                onSub.onSuccess(null);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
