package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.GrotesqueStatueArtifact;
import sk.sivak.eldritchhorror.core.constants.artifact.PallidMaskArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetEventListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.WhiskeyListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.GainArtifactFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.card.GainedCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GrotesqueStatueListener extends AbstractArtifactListener<GrotesqueStatueArtifact> {

    private static final Logger logger = LogManager.getLogger(GrotesqueStatueListener.class);
    private EnableCardListener enableCardListener;
    private BeforeLoseSanityListener beforeLoseSanityListener;


    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(enableCardListener, beforeLoseSanityListener);
    }

    @Override
    protected void register() {
        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

        beforeLoseSanityListener = new BeforeLoseSanityListener();
        getEventQueue().addBeforeEventListener(beforeLoseSanityListener, BeforeAfterEvent.LOSE_SANITY);

        ServicePlatform.get().getService().<GainedCardData>addEventCommand(input -> {
            ServicePlatform.get().getService().hold();
            for (int i = 0; i < 5; i++) {
                ServicePlatform.get().getTokenService().gainClueFromPool();
            }
            ServicePlatform.get().getService().convertTo(GainedCardData.class, () -> input);
            ServicePlatform.get().getService().release();
        });
    }

    @Override
    protected void justLoad() {
        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);

        beforeLoseSanityListener = new BeforeLoseSanityListener();
        getEventQueue().addBeforeEventListener(beforeLoseSanityListener, BeforeAfterEvent.LOSE_SANITY);
    }

    private class BeforeLoseSanityListener extends AbstractArtifactEventListener<LoseTokenData> {

        BeforeLoseSanityListener() {
            super(GrotesqueStatueListener.this);
        }

        @Override
        protected Runnable getEventAction(LoseTokenData input) {
            return () -> {
                if (!enabled) {
                    return;
                }
                if (input.getAmount() <= 0) {
                    return;
                }
                ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(canSpend -> {
                    doWithCanSpendData(canSpend, input);
                });
            };
        }

        private void doWithCanSpendData(SpendData canSpend, LoseTokenData input) {
            if (canSpend.hasEnough()) {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setTitle("Prevent all Sanity loss for a Clue?");
                showCardRequest.setHideType(HideType.DISABLE_ON_YES);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);

                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            } else {
                ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
            }
        }

        private void onAnswer(ShowCardResponse showCardResponse, LoseTokenData input) {
            if (ShowCardResponse.YES == showCardResponse) {
                ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spendData -> doWithSpendData(spendData, input));
            } else {
                ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
            }
        }

        private void doWithSpendData(SpendData spendData, LoseTokenData input) {
            if (spendData.hasEnough()) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                spendData.pay();
                input.setAmount(0);
                ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
            }
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }
    }
}
