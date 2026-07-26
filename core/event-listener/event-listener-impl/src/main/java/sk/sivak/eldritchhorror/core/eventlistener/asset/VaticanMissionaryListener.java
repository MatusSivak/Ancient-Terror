package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.VaticanMissionaryAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.List;

/**
 * @author msivak
 */
public class VaticanMissionaryListener extends AbstractAssetListener<VaticanMissionaryAsset> {

    private static final Logger logger = LogManager.getLogger(VaticanMissionaryListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private RerollUsingAssetsListener rerollUsingAssetsListener;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
        rerollUsingAssetsListener = new RerollUsingAssetsListener();
        getEventQueue().addDirectEventListener(rerollUsingAssetsListener, DirectEvent.REROLL_USING_ASSETS_1);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, rerollUsingAssetsListener);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(VaticanMissionaryListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.WILL) {
                    return;
                }
                addUsableAsset(input, getAssetInfo(), 1);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class RerollUsingAssetsListener extends AbstractAssetEventListener<TestData> {

        public RerollUsingAssetsListener() {
            super(VaticanMissionaryListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.hasReachedMinScoreToEndTest()) {
                    return;
                }
                if (input.getStat() != Stat.WILL) {
                    return;
                }
                if (input.getCalculatedDicePool() <= input.getScore()) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Reroll 1 die?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, TestData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getTestService().rerollDie(input);
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
