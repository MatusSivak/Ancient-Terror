package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.PocketWatchAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class PocketWatchListener extends AbstractAssetListener<PocketWatchAsset> {

    private static final Logger logger = LogManager.getLogger(PocketWatchListener.class);
    private BeforeDelayedListener beforeDelayedListener;

    @Override
    protected void register() {
        beforeDelayedListener = new BeforeDelayedListener();
        getEventQueue().addBeforeEventListener(beforeDelayedListener, BeforeAfterEvent.BECOME_DELAYED);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(beforeDelayedListener);
    }

    private class BeforeDelayedListener extends AbstractAssetEventListener<DelayedData> {

        public BeforeDelayedListener() {
            super(PocketWatchListener.this);
        }

        @Override
        protected Runnable getEventAction(DelayedData input) {
            return () -> {
                if (!input.isForced()) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("You cannot become Delayed");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, DelayedData input) {
            ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.BECOME_DELAYED, input);
        }

        @Override
        public Class<DelayedData> getDataClass() {
            return DelayedData.class;
        }
    }
}
