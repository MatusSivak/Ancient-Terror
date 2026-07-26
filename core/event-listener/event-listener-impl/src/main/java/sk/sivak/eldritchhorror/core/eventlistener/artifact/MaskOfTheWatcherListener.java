package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.MaskOfTheWatcherArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

import java.util.Arrays;
import java.util.List;

public class MaskOfTheWatcherListener extends AbstractArtifactListener<MaskOfTheWatcherArtifact> {

    private static final Logger logger = LogManager.getLogger(MaskOfTheWatcherListener.class);
    private ReduceHorrorListener reduceHorrorListener;
    private AfterHorrorTestListener afterHorrorTestListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(reduceHorrorListener, afterHorrorTestListener);
    }

    @Override
    protected void register() {
        reduceHorrorListener = new ReduceHorrorListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(reduceHorrorListener, BeforeAfterEvent.UPDATE_MONSTER_HORROR);

        afterHorrorTestListener = new AfterHorrorTestListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterHorrorTestListener, BeforeAfterEvent.AFTER_HORROR_CHECK);
    }

    private class ReduceHorrorListener extends AbstractArtifactEventListener<CombatData> {


        ReduceHorrorListener() {
            super(MaskOfTheWatcherListener.this);
        }

        @Override
        protected Runnable getEventAction(CombatData input) {
            return () -> {
                if (input.getActualHorror() == null) {
                    return;
                }
                if (input.getActualHorror() <= 1) {
                    return;
                }

                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setTitle("Reduce the Horror of Monsters you encounter to 1.");
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);

                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse ->
                        onShowCard(input));
            };
        }

        private void onShowCard(CombatData input) {
            input.setActualHorror(1);
            ServicePlatform.get().getService().convertTo(CombatData.class, () -> input);
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

    private class AfterHorrorTestListener extends AbstractArtifactEventListener<CombatData> {

        public AfterHorrorTestListener() {
            super(MaskOfTheWatcherListener.this);
        }

        @Override
        protected Runnable getEventAction(CombatData input) {
            return () -> {
                if (input.getHorrorTestResult().getStat() != Stat.WILL) {
                    return;
                }
                if (!input.getHorrorTestResult().isSuccessful()) {
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.RETURN);
                showCardRequest.setTitle("Gain Focus.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTokenService().gainFocus();
                    ServicePlatform.get().getTokenService().convertTo(CombatData.class, () -> input);
                    ServicePlatform.get().getService().release();
                });
            };
        }

        @Override
        public Class<CombatData> getDataClass() {
            return CombatData.class;
        }
    }

}
