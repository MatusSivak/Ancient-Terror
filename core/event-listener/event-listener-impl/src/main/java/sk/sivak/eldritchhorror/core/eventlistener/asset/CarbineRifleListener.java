package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.asset.CarbineRifleAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
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
public class CarbineRifleListener extends AbstractAssetListener<CarbineRifleAsset> {

    private static final Logger logger = LogManager.getLogger(CarbineRifleListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, enableCardListener);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(CarbineRifleListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!enabled) {
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
                request.setHideType(HideType.DISABLE_ALWAYS);
                request.setAssetInfo(getAssetInfo());
                request.setTitle("Carbine Rifle used");
                request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
                ServicePlatform.get().getGameService().showCard(request).subscribe(showCardResponse -> onUseInCombat(showCardResponse, input));
            };
        }

        void onUseInCombat(ShowCardResponse showCardResponse, TestData input) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(getAssetInfo());
            ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
