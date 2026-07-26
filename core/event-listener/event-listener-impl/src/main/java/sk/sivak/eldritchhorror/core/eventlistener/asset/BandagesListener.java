package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.BandagesAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.TokenType;
import sk.sivak.eldritchhorror.core.service.GameService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java8.features.util.MapUtils.getOrDefault;

/**
 * @author msivak
 */
public class BandagesListener extends AbstractAssetListener<BandagesAsset> {

    private static final Logger logger = LogManager.getLogger(BandagesListener.class);
    private BeforeLoseHealthListener beforeLoseHealthListener;

    @Override
    protected void register() {
        beforeLoseHealthListener = new BeforeLoseHealthListener();
        getEventQueue().addBeforeEventListener(beforeLoseHealthListener, BeforeAfterEvent.LOSE_HEALTH);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(beforeLoseHealthListener);
    }

    private class BeforeLoseHealthListener extends AbstractAssetEventListener<LoseTokenData> {

        public BeforeLoseHealthListener() {
            super(BandagesListener.this);
        }

        @Override
        public void onNotify(LoseTokenData eventData) {
            ServicePlatform.get().getService().<LoseTokenData>addEventCommand((input) -> {
                getEventAction(input).run();
            });
        }

        @Override
        protected Runnable getEventAction(LoseTokenData input) {
            return () -> {
                if (input.getAmount() <= 0) {
                    return;
                }
                LocationId activeLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                LocationId ownerLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getCurrentLocationId();
                if (activeLocationId != ownerLocationId) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setTitle("Use Bandages?");
                showCardRequest.setHideType(HideType.DISCARD_ON_YES);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));

            };
        }

        private void onAnswer(ShowCardResponse showCardResponse, LoseTokenData input) {
            ServicePlatform.get().getService().hold();
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                input.setAmount(Math.max(0, input.getAmount() - 2));
            }
            ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }
    }
}
