package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.DholChantsArtifact;
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

public class DholChantsListener extends AbstractArtifactListener<DholChantsArtifact> {

    private static final Logger logger = LogManager.getLogger(DholChantsListener.class);
    private RegisterUsableAssetsListener registerUsableAssetsListener;


    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(registerUsableAssetsListener);

    }

    @Override
    protected void register() {
        registerUsableAssetsListener = new RegisterUsableAssetsListener();
        getEventQueue().addDirectEventListener(registerUsableAssetsListener, DirectEvent.REGISTER_USABLE_ASSETS);
    }

    private class RegisterUsableAssetsListener extends AbstractArtifactEventListener<TestData> {

        public RegisterUsableAssetsListener() {
            super(DholChantsListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (ServicePlatform.get().getTestFlavor().getFlavorType() != TestFlavorType.COMBAT) {
                    return;
                }
                ServicePlatform.get().getTokenService().canSpend(0,0,0,1).subscribe(canSpend -> {
                    if (!canSpend.hasEnough()) {
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                        return;
                    }
                    ShowCardRequest showCardRequest = new ShowCardRequest();
                    showCardRequest.setArtifactInfo(getArtifactInfo());
                    showCardRequest.setTitle("Spend Sanity to roll additional dice?");
                    showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                    showCardRequest.setHideType(HideType.RETURN);
                    ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> {
                        if (ShowCardResponse.YES != showCardResponse) {
                            ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                            return;
                        }
                        testLore(input);
                    });
                });
            };
        }

        private void testLore(TestData input) {
            ServicePlatform.get().getTestService().test(Stat.LORE, 0, 1).subscribe(testResult -> {
                if (!testResult.isSuccessful()) {
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                    return;
                }
                ServicePlatform.get().getTokenService().spend(0,0,0,1).subscribe(spendData -> {
                    if (!spendData.hasEnough()) {
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                        return;
                    }
                    ServicePlatform.get().getService().hold();
                    UsableAsset usableAsset = new UsableAsset();
                    usableAsset.setCardInfo(getArtifactInfo());
                    usableAsset.setStatBonus(3);
                    usableAsset.setUpByDefault(true);
                    input.addUsableAsset(usableAsset);
                    spendData.pay();
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> input);
                    ServicePlatform.get().getService().release();

                });
            });
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

}
