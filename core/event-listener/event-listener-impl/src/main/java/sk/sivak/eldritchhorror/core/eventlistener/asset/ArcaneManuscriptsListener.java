package sk.sivak.eldritchhorror.core.eventlistener.asset;

import sk.sivak.eldritchhorror.core.constants.asset.ArcaneManuscriptsAsset;
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
public class ArcaneManuscriptsListener extends AbstractAssetListener<ArcaneManuscriptsAsset> {

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
            super(ArcaneManuscriptsListener.this);
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
                addUsableAsset(input, getAssetInfo(), 1);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
