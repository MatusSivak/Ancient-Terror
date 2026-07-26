package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.asset.CharterFlightAsset;
import sk.sivak.eldritchhorror.core.constants.asset.SearchTheArchivesAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.TravelData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class SearchTheArchivesListener extends AbstractAssetListener<SearchTheArchivesAsset> {

    private static final Logger logger = LogManager.getLogger(SearchTheArchivesListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand((input) -> {
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setAssetInfo(getAssetInfo());
            showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
            showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
            showCardRequest.setTitle("Gain one Tome.");
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
        AssetInfo tomeAsset = ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.TOME);
        ServicePlatform.get().getGameService().gainAsset(tomeAsset);
        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }
}
