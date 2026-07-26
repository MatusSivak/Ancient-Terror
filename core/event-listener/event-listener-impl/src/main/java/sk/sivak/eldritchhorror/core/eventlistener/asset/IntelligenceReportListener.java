package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.IntelligenceReportAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class IntelligenceReportListener extends AbstractAssetListener<IntelligenceReportAsset> {

    private static final Logger logger = LogManager.getLogger(IntelligenceReportListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand((input) -> {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
            showCardRequest.setAssetInfo(getAssetInfo());
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
            showCardRequest.setTitle("Gain 2 Clues");
            ServicePlatform.get().getGameService().showCard(showCardRequest)
                    .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
        });
    }

    @Override
    public boolean gainCard() {
        return false;
    }

    private void onAnswer(ShowCardResponse showCardResponse, Object input) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
        ServicePlatform.get().getTokenService().gainClueFromPool();
        ServicePlatform.get().getTokenService().gainClueFromPool();
        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }
}
