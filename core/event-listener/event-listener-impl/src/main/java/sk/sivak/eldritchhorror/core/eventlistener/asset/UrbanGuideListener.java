package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.UrbanGuideAsset;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class UrbanGuideListener extends AbstractAssetListener<UrbanGuideAsset> {

    private static final Logger logger = LogManager.getLogger(UrbanGuideListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(registerUsableAssetListener);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(UrbanGuideListener.this);
        }

        @Override
        public void onNotify(TestData eventData) {
            ServicePlatform.get().getService().<TestData>addEventCommand((input) -> {
                getEventAction(input).run();
            });
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                LocationId activeLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                LocationId ownerLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getCurrentLocationId();
                LocationInfo activeLocation = ServicePlatform.get().getLocationMap().getLocationInfo(activeLocationId);

                if (LocationType.CITY != activeLocation.getLocationType()) {
                    return;
                }
                if (isTestFlavorOfType(TestFlavorType.OTHER_WORLD)) {
                    return;
                }
                if (activeLocationId != ownerLocationId) {
                    return;
                }
                addUsableAsset(input, getAssetInfo(), 0, 1);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
