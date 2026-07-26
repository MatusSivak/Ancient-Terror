package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.AstronomyGuidebookAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CLOSE_GATE;

/**
 * @author msivak
 */
public class AstronomyGuidebookListener extends AbstractAssetListener<AstronomyGuidebookAsset> {

    private static final Logger logger = LogManager.getLogger(AstronomyGuidebookListener.class);
    private AfterCloseGateListener afterCloseGateListener;

    @Override
    protected void register() {
        afterCloseGateListener = new AfterCloseGateListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterCloseGateListener, CLOSE_GATE);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(afterCloseGateListener);
    }

    private class AfterCloseGateListener extends AbstractAssetEventListener<CloseGateData> {


        AfterCloseGateListener() {
            super(AstronomyGuidebookListener.this);
        }

        @Override
        protected Runnable getEventAction(CloseGateData input) {
            return () -> {
                if (!input.isOtherworldEncounter()) {
                    return;
                }
                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getAssetInfo())
                        .withTitle("Recover one Sanity and gain one Clue.")
                        .build())
                        .subscribe(showCardResponse -> onAnswer(input));
            };
        }

        private void onAnswer(CloseGateData input) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().gainSanity(1);
            ServicePlatform.get().getTokenService().gainClueFromPool();
            ServicePlatform.get().getService().convertTo(CloseGateData.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<CloseGateData> getDataClass() {
            return CloseGateData.class;
        }
    }
}
