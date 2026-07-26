package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.SanctuaryAsset;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class SanctuaryListener extends AbstractAssetListener<SanctuaryAsset> {

    private static final Logger logger = LogManager.getLogger(SanctuaryListener.class);

    @Override
    protected void register() {
        ServicePlatform.get().getService().addEventCommand((input) -> {
            if (hasCondition()) {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
                showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setTitle("Discard any Condition?");
                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onDiscardOrNo(showCardResponse, input));
            } else {
                //ok
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
                showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                showCardRequest.setTitle("You don't have any condition.");
                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onNoCondition(showCardResponse, input));
            }
        });
    }

    private boolean hasCondition() {
        InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
        return ServicePlatform.get().getConditionsDeck().hasAnyCondition(activeInvestigatorId);
    }

    @Override
    public boolean gainCard() {
        return false;
    }

    private void onDiscardOrNo(ShowCardResponse showCardResponse, Object input) {
        if (ShowCardResponse.NO == showCardResponse) {
            onNoCondition(showCardResponse, input);
        } else {
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setHideText("Display conditions?");
            selectCardData.setTitleText("Select Condition to discard");
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            selectCardData.setAvailableCards(ServicePlatform.get().getConditionsDeck().getConditions(activeInvestigatorId));
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData)
                    .subscribe(cardInfo -> onSelectedCondition(cardInfo, input));
        }
    }

    private void onSelectedCondition(CardInfo cardInfo, Object input) {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.DISCARD_ALWAYS);
        ConditionInfo conditionInfo = (ConditionInfo) cardInfo;
        request.setConditionInfo(conditionInfo);
        request.setTitle("Discard " + cardInfo.getName());
        request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
        ServicePlatform.get().getGameService().showCard(request)
                .subscribe(out -> onDiscardConditionConfirm(out, conditionInfo, input));
    }

    private void onDiscardConditionConfirm(ShowCardResponse showCardResponse, ConditionInfo conditionInfo, Object input) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, conditionInfo);
        ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();
    }

    private void onNoCondition(ShowCardResponse showCardResponse, Object input) {
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
