package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.ArcaneBladeAsset;
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
public class ArcaneBladeListener extends AbstractAssetListener<ArcaneBladeAsset> {

    private static final Logger logger = LogManager.getLogger(ArcaneBladeListener.class);
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
            super(ArcaneBladeListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() == Stat.STRENGTH) {
                    onStrengthTest(input);
                    return;
                }
                if (input.getStat() == Stat.LORE) {
                    onLoreTest(input);
                }
            };
        }

        private void onStrengthTest(TestData input) {
            if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                return;
            }
            addUsableAsset(input, getAssetInfo(), 2);
        }


        private void onLoreTest(TestData input) {
            if (!isTestFlavorOfType(TestFlavorType.SPELL)) {
                return;
            }
            addUsableAsset(input, getAssetInfo(), 2);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
