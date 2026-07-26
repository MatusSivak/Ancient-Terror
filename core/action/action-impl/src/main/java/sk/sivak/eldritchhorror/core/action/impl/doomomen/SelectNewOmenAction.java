package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Single;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class SelectNewOmenAction implements Action<Object, OmenInfo> {

    @Override
    public Single<OmenInfo> execute() {
        return Single.create(onSub -> {
            ServicePlatform.get().getDoomOmenController().selectNewOmen().subscribe(omenId -> {
                ServicePlatform.get().getOmenTrack().setCurrentOmen(omenId);
                OmenInfo omenInfo = ServicePlatform.get().getOmenTrack().getOmenInfo(omenId);
                ServicePlatform.get().getDoomOmenController().updateNoiseColorAndGatesTransparency(omenInfo.getOmenColor());
                onSub.onSuccess(omenInfo);
            });
        });
    }

    @Override
    public void setInput(Object input) {

    }
}
