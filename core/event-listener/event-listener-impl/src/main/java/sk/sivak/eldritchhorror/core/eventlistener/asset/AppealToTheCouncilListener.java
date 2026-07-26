package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AppealToTheCouncilAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class AppealToTheCouncilListener extends AbstractAssetListener<AppealToTheCouncilAsset> {

    private static final Logger logger = LogManager.getLogger(AppealToTheCouncilListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand((input) -> {
            Single<SpendData> canSpend = ServicePlatform.get().getTokenService().canSpend(2, 0, 0, 0);
            canSpend.subscribe(spendData -> onCanSpendResponse(spendData, input));
        });
    }

    @Override
    public boolean gainCard() {
        return false;
    }

    private void onCanSpendResponse(SpendData spendData, Object input) {
        if (spendData.hasEnough()) {
            spendCluesQuestion(input);
        } else {
            notEnoughClues(input);
        }
    }

    private void notEnoughClues(Object input) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
        showCardRequest.setTitle("Not enough Clues");
        ServicePlatform.get().getGameService().showCard(showCardRequest)
                .subscribe(showCardResponse -> discard(showCardResponse, input));
    }

    private void spendCluesQuestion(Object input) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
        showCardRequest.setTitle("Spend Clues to retreat Doom?");
        ServicePlatform.get().getGameService().showCard(showCardRequest)
                .subscribe(showCardResponse -> onYesNo(showCardResponse, input));
    }

    private void onYesNo(ShowCardResponse showCardResponse, Object input) {
        if (showCardResponse == ShowCardResponse.NO) {
            discard(showCardResponse, input);
        } else {
            Single<SpendData> spend = ServicePlatform.get().getTokenService().spend(2, 0, 0, 0);
            spend.subscribe(spendData -> onSpendResponse(spendData, input));
        }
    }

    private void onSpendResponse(SpendData spendData, Object input) {
        if (!spendData.hasEnough()) {
            notEnoughClues(input);
            return;
        }
        ServicePlatform.get().getGameService().hold();
        spendData.pay();
        ServicePlatform.get().getDoomOmenService().retreatDoom();
        discard(null, input);
        ServicePlatform.get().getGameService().release();
    }

    private void discard(ShowCardResponse showCardResponse, Object input) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }
}
