package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.SpiritDaggerAsset;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class SpiritDaggerListener extends AbstractAssetListener<SpiritDaggerAsset> {

    private static final Logger logger = LogManager.getLogger(SpiritDaggerListener.class);
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
            super(SpiritDaggerListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                if (input.getStat() == Stat.WILL) {
                    addUsableAsset(input, getAssetInfo(), 1);
                } else if (input.getStat() == Stat.STRENGTH) {
                    addUsableAsset(input, getAssetInfo(), 2);
                }
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
