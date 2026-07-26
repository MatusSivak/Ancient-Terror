package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.LuckyRabbitsFootAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class LuckyRabbitsFootListener extends AbstractAssetListener<LuckyRabbitsFootAsset> {

    private static final Logger logger = LogManager.getLogger(LuckyRabbitsFootListener.class);
    private RerollUsingAssetsListener rerollUsingAssetsListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_2);
        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(rerollUsingAssetsListener, enableCardListener);
    }

    private class RerollUsingAssetsListener extends AbstractAssetEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(LuckyRabbitsFootListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (!enabled) {
                    return;
                }
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.DISABLE_ON_YES);
                showCardRequest.setTitle("Reroll 1 die?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getAssetInfo());
                ServicePlatform.get().getTestService().rerollDie(input);
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().convertFromTo(ShowCardResponse.class, TestData.class, response -> input);
            }

        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
