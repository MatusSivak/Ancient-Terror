package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.asset.Dot45AutomaticAsset;
import sk.sivak.eldritchhorror.core.constants.asset.MineralogyResearchAsset;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.ExpeditionEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.GeneralEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class MineralogyResearchListener extends AbstractAssetListener<MineralogyResearchAsset> {

    private static final Logger logger = LogManager.getLogger(MineralogyResearchListener.class);
    private BeforeEndOfEncounterListener beforeEndOfEncounterListener;

    @Override
    protected void register() {
        beforeEndOfEncounterListener = new BeforeEndOfEncounterListener();
        getEventQueue().addBeforeEventListener(beforeEndOfEncounterListener, BeforeAfterEvent.END_OF_ENCOUNTER);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(beforeEndOfEncounterListener);
    }

    private class BeforeEndOfEncounterListener extends AbstractAssetEventListener<Object> {

        BeforeEndOfEncounterListener() {
            super(MineralogyResearchListener.this);
        }

        @Override
        protected Runnable getEventAction(Object input) {
            return () -> {
                if (!(input instanceof Encounter)) {
                    return;
                }
                if (((Encounter) input).getEncounterType() != EncounterType.GENERAL && ((Encounter) input).getEncounterType() != EncounterType.EXPEDITION) {
                    return;
                }
                if (((Encounter) input).getEncounterType() == EncounterType.GENERAL && ((GeneralEncounter) input).getLocationType() != LocationType.WILDERNESS) {
                    return;
                }
                if (((Encounter) input).getEncounterType() == EncounterType.EXPEDITION &&
                        ServicePlatform.get().getLocationMap().getLocationInfo(((ExpeditionEncounter) input).getLocationId()).getLocationType() != LocationType.WILDERNESS ) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setTitle("Examine the area's soil?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setAssetOriginType(AssetOriginType.INVENTORY);
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(response -> {
                    if (response == ShowCardResponse.NO) {
                        ServicePlatform.get().getService().convertTo(Object.class, () -> input);
                        return;
                    }
                    ServicePlatform.get().getTestService().test(Stat.OBSERVATION, 0, 1).subscribe(testResult -> {
                        if (!testResult.isSuccessful()) {
                            ServicePlatform.get().getService().convertTo(Object.class, () -> input);
                            return;
                        }
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getTokenService().gainClueFromPool();
                        ServicePlatform.get().getTokenService().gainClueFromPool();
                        ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getAssetInfo())
                                .withTitle("Discard this card.")
                                .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                                .withAssetHideType(HideType.DISCARD_ALWAYS)
                                .build())
                                .subscribe(showCardResponse -> {
                                    ServicePlatform.get().getService().hold();
                                    ServicePlatform.get().getService().discardAssetFromInvestigator(investigatorId, getAssetInfo(), false);
                                    ServicePlatform.get().getService().convertTo(Object.class, () -> input);
                                    ServicePlatform.get().getService().release();
                                });
                        ServicePlatform.get().getService().release();
                    });
                });
            };
        }

        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }
}
