package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.asset.KeroseneAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

public class KeroseneListener extends AbstractAssetListener<KeroseneAsset> {
    private static final Logger logger = LogManager.getLogger(KeroseneListener.class);

    private RegisterUsableAssetListener registerUsableAssetListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(registerUsableAssetListener);
    }

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(KeroseneListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                UsableAsset usableAsset = addUsableAsset(input, getAssetInfo(), 5);
                usableAsset.setOnUseAction(getOnUseAction(input));
            };
        }

        private Action0 getOnUseAction(TestData input) {
            return () -> {
                ShowCardRequest request = new ShowCardRequest();
                request.setHideType(HideType.DISCARD_ALWAYS);
                request.setAssetInfo(getAssetInfo());
                request.setTitle("Discard Kerosene");
                request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
                ServicePlatform.get().getGameService().showCard(request)
                        .subscribe(showCardResponse -> onDiscardAssetConfirm(showCardResponse, input));
            };
        }

        void onDiscardAssetConfirm(ShowCardResponse showCardResponse, TestData input) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
            ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
