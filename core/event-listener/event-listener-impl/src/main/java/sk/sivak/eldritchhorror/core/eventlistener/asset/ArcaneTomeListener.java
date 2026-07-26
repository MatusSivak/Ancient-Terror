package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.ArcaneTomeAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse.YES;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

/**
 * @author msivak
 */
public class ArcaneTomeListener extends AbstractAssetListener<ArcaneTomeAsset> {

    private static final Logger logger = LogManager.getLogger(ArcaneTomeListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private ArcaneTomeAfterRest arcaneTomeAfterRest;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
        arcaneTomeAfterRest = new ArcaneTomeAfterRest();
        getEventQueue().addAfterEventListener(arcaneTomeAfterRest, BeforeAfterEvent.REST);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, arcaneTomeAfterRest);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(ArcaneTomeListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.LORE) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.SPELL)) {
                    return;
                }
                addUsableAsset(input, getAssetInfo(), 2);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class ArcaneTomeAfterRest extends AbstractAssetEventListener<RestData> {

        private ArcaneTomeAfterRest() {
            super(ArcaneTomeListener.this);
        }

        @Override
        protected Runnable getEventAction(RestData input) {
            return () -> {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setTitle("Read Arcane Tome?");
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(response -> onAnswer(response, input));
            };
        }

        void onAnswer(ShowCardResponse showCardResponse, RestData input) {
            if (showCardResponse != YES) {
                ServicePlatform.get().getService().convertTo(RestData.class, () -> input);
                return;
            }

            Single<TestData> test = ServicePlatform.get().getTestService().test(Stat.LORE, 0, JUST_ONE);
            test.subscribe(testData -> {
                if (testData.isSuccessful()) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getGameService().gainSpell();
                    ServicePlatform.get().getService().convertTo(RestData.class, () -> input);
                    ServicePlatform.get().getService().release();
                } else {
                    ServicePlatform.get().getService().convertTo(RestData.class, () -> input);
                }
            });
        }

        @Override
        public Class<RestData> getDataClass() {
            return RestData.class;
        }
    }
}
