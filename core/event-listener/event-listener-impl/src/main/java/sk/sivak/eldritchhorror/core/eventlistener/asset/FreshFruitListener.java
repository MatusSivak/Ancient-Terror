package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.FreshFruitAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class FreshFruitListener extends AbstractAssetListener<FreshFruitAsset> {

    private static final Logger logger = LogManager.getLogger(FreshFruitListener.class);

    private FreshFruitAfterRest freshFruitAfterRestListener;

    @Override
    protected void register() {
        freshFruitAfterRestListener = new FreshFruitAfterRest();
        getEventQueue().addAfterEventListener(freshFruitAfterRestListener, BeforeAfterEvent.REST);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(freshFruitAfterRestListener);
    }

    private class FreshFruitAfterRest extends AbstractAssetEventListener<RestData> {

        private FreshFruitAfterRest() {
            super(FreshFruitListener.this);
        }

        @Override
        protected Runnable getEventAction(RestData input) {
            return () -> {
                InvestigatorRead investigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
                if (investigator.getCurrentHealth() == investigator.getInfo().getMaxHealth()
                        && investigator.getCurrentSanity() == investigator.getInfo().getMaxSanity()) {
                    return;
                }
                if (!input.isHealthEnabled() && !input.isSanityEnabled()) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.DISCARD_ON_YES);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setTitle("Consume Fresh Fruit?");
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        void onAnswer(ShowCardResponse showCardResponse, RestData input) {
            if (showCardResponse != ShowCardResponse.YES) {
                ServicePlatform.get().getService().convertFromTo(Object.class, RestData.class, out -> input);
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
            if (input.isHealthEnabled()) {
                ServicePlatform.get().getTokenService().gainHealth(1);
            }
            if (input.isSanityEnabled()) {
                ServicePlatform.get().getTokenService().gainSanity(1);
            }
            ServicePlatform.get().getService().convertFromTo(Object.class, RestData.class, out -> input);
            ServicePlatform.get().getService().release();

        }

        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }
}
