package sk.sivak.eldritchhorror.core.action.impl.reserve;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.AssetsDeckWrite;

public class RefillReserveAction extends AbstractHookableAction<Object, Void> {

    private static final Logger logger = LogManager.getLogger(RefillReserveAction.class);

    public RefillReserveAction() {
        super(BeforeAfterEvent.REFILL_RESERVE);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super Void> ss) {
        AssetsDeckWrite assetsDeck = ServicePlatform.get().getAssetsDeck();
        assetsDeck.initReserve();
        ss.onSuccess(null);
    }

}
