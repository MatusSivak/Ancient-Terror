package sk.sivak.eldritchhorror.core.action.impl.reserve;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.reserve.ShowReserveDragAndDropOutput;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.DiscardAssetFromInvestigatorData;

public class RegisterSelectedAssetsAction extends AbstractHookableAction<ShowReserveDragAndDropOutput, ShowReserveDragAndDropOutput> {

    private static final Logger logger = LogManager.getLogger(DiscardAssetFromInvestigatorData.class);

    public RegisterSelectedAssetsAction() {
        super(BeforeAfterEvent.REGISTER_SELECTED_ASSETS);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super ShowReserveDragAndDropOutput> ss) {
        ServicePlatform.get().getGameService().hold();
        for (AssetInfo assetInfo : input.getSelectedAssets()) {
            ServicePlatform.get().getGameService().gainAsset(assetInfo);
        }
        ServicePlatform.get().getGameService().convertTo(ShowReserveDragAndDropOutput.class, () -> input);
        ServicePlatform.get().getGameService().release();
        ss.onSuccess(input);
    }

}
