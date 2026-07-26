package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java8.features.stream.Stream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.PallidMaskArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;

import java.util.Collections;
import java.util.List;

public class PallidMaskListener extends AbstractArtifactListener<PallidMaskArtifact> {

    private static final Logger logger = LogManager.getLogger(PallidMaskListener.class);
    private HideCombatEncountersListener hideCombatEncountersListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Collections.singletonList(hideCombatEncountersListener);

    }

    @Override
    protected void register() {
        hideCombatEncountersListener = new HideCombatEncountersListener();
        getEventQueue().addBeforeEventListener(hideCombatEncountersListener, BeforeAfterEvent.DISABLE_EPIC_COMBAT_ENCOUNTERS);
    }

    private class HideCombatEncountersListener extends AbstractArtifactEventListener<AvailableEncounters> {

        public HideCombatEncountersListener() {
            super(PallidMaskListener.this);
        }

        @Override
        protected Runnable getEventAction(AvailableEncounters input) {
            return () -> {
                if (!Stream.anyMatch(input.getEncounters(), encounter -> encounter.getEncounterType() == EncounterType.COMBAT)) {
                    return;
                }
                if (ServicePlatform.get().getPerformedEncounters().hasEncounteredAnything(investigatorId)) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setTitle("Ignore Monsters?");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
                showCardRequest.setHideType(HideType.RETURN);
                ServicePlatform.get().getGameService().showCard(showCardRequest)
                        .subscribe(showCardResponse -> afterShowCard(showCardResponse, input));
            };
        }

        private void afterShowCard(ShowCardResponse response, AvailableEncounters input) {
            if (ShowCardResponse.YES != response) {
                ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> input);
                return;
            }
            input.removeEncounters(encounter -> encounter.getEncounterType() == EncounterType.COMBAT);
            ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> input);
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }
}
