package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;

public class OnTriggeredReckoningAction implements Action<Object, Void> {

    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            if (!ServicePlatform.get().getModel().isReckoningTriggered()) {
                ServicePlatform.get().getModel().setReckoningTriggered(true);
                ServicePlatform.get().getGameController().showWorldBackground().subscribe();
                ServicePlatform.get().getModel().getBackgroundModel().setCurrentBackground(null);
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
            }
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {
        //
    }
}
