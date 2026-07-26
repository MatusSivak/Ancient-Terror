package sk.sivak.eldritchhorror.core.eventlistener.asset;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.asset.DeliveryServiceAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.trade.TradeData;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class DeliveryServiceListener extends AbstractAssetListener<DeliveryServiceAsset> {

    private static final Logger logger = LogManager.getLogger(DeliveryServiceListener.class);

    private AfterFindListener afterFindListener;
    private AfterCollectItemsListener afterCollectItemsListener;

    @Override
    protected void register() {
        ServicePlatform.get().getService().<GainedCardData>addEventCommand((input) -> {
            afterFindListener = new AfterFindListener();
            afterCollectItemsListener = new AfterCollectItemsListener();

            ServicePlatform.get().getEventQueue().addAfterEventListener(afterFindListener, BeforeAfterEvent.FIND_INVESTIGATORS_FOR_TRADE);
            ServicePlatform.get().getEventQueue().addAfterEventListener(afterCollectItemsListener, BeforeAfterEvent.COLLECT_ITEMS_FOR_TRADE);

            displayCard(input);
        });
    }

    private void displayCard(Object input) {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setAssetInfo(getAssetInfo());
        showCardRequest.setAssetOriginType(AssetOriginType.CENTER);
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
        showCardRequest.setTitle("Deliver Item possessions");
        ServicePlatform.get().getGameService().showCard(showCardRequest)
                .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
    }

    private void onAnswer(ShowCardResponse showCardResponse, Object input) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
        ServicePlatform.get().getBasicActionService().trade();
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getEventQueue().unregisterListener(afterFindListener);
            ServicePlatform.get().getEventQueue().unregisterListener(afterCollectItemsListener);

        });
        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
        ServicePlatform.get().getService().release();
    }

    @Override
    public boolean gainCard() {
        return false;
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.emptyList();
    }

    private class AfterFindListener extends EventListenerImpl<List> {

        @Override
        public void onNotify(List eventData) {
            List<InvestigatorId> investigatorIds = eventData;
            investigatorIds.clear();
            List<? extends InvestigatorRead> selectedInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            Collection<InvestigatorId> selectedInvestigatorsIds =
                    Stream.map(selectedInvestigators, in -> in.getInfo().getInvestigatorId());
            IterableUtils.removeIf(selectedInvestigatorsIds, in -> in == investigatorId);
            investigatorIds.addAll(selectedInvestigatorsIds);
        }

        @Override
        public Class<List> getDataClass() {
            return List.class;
        }
    }

    private class AfterCollectItemsListener extends EventListenerImpl<TradeData> {

        @Override
        public void onNotify(TradeData eventData) {
            IterableUtils.removeIf(eventData.getSourceCards(), cardInfo -> !cardInfo.getTraits().contains(AssetTrait.ITEM));
            eventData.getTargetCards().clear();
            TradeData.TokenTrading targetTokenTrading = eventData.getTargetTokenTrading();
            if (targetTokenTrading == null) {
                return;
            }
            if (targetTokenTrading.getBeforeTrade() == null) {
                return;
            }
            targetTokenTrading.getBeforeTrade().setClues(0);
            targetTokenTrading.getBeforeTrade().setTrainTickets(0);
            targetTokenTrading.getBeforeTrade().setShipTickets(0);
        }

        @Override
        public Class<TradeData> getDataClass() {
            return TradeData.class;
        }
    }
}
