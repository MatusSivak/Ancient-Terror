package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.LavishFeastAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class LavishFeastListener extends AbstractAssetListener<LavishFeastAsset> {

    private static final Logger logger = LogManager.getLogger(LavishFeastListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand((input) -> {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(getAssetInfo());
            showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
            showCardRequest.setTitle("Restore Health and Sanity");
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
        ServicePlatform.get().getTokenService().gainHealth(2);
        ServicePlatform.get().getTokenService().gainSanity(2);
        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();

    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }
}
