package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class AncientOneAwakensAction implements Action<Object, Void> {



    @Override
    public Single<Void> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getModel().getAncientOne().awaken();
            AncientOneInfo ancientOneInfo = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo();
            ServicePlatform.get().getEventListenerProvider().registerAncientOneListeners(ancientOneInfo);
            onSub.onSuccess(null);
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
