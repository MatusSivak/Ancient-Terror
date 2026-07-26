package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.artifact.GateBoxArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth.OmenOfDevastationListener;
import sk.sivak.eldritchhorror.core.eventlistener.asset.AbstractAssetEventListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CloseGateData;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.CLOSE_GATE;

/**
 * @author msivak
 */
public class GateBoxListener extends AbstractArtifactListener<GateBoxArtifact> {

    private static final Logger logger = LogManager.getLogger(GateBoxListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private AfterCloseGateListener afterCloseGateListener;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);

        afterCloseGateListener = new AfterCloseGateListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterCloseGateListener, CLOSE_GATE);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, afterCloseGateListener);
    }

    private class RegisterUsableAssetListener extends AbstractArtifactEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(GateBoxListener.this);
        }

        @Override
        public void onNotify(TestData eventData) {
            ServicePlatform.get().getService().<TestData>addEventCommand((input) -> {
                getEventAction(input).run();
            });
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                LocationId activeLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                LocationId ownerLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).getCurrentLocationId();
                if (!isTestFlavorOfType(TestFlavorType.OTHER_WORLD)) {
                    return;
                }
                if (activeLocationId != ownerLocationId) {
                    return;
                }
                addUsableAsset(input, getArtifactInfo(), 0, 1);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class AfterCloseGateListener extends AbstractArtifactEventListener<CloseGateData> {


        AfterCloseGateListener() {
            super(GateBoxListener.this);
        }

        @Override
        protected Runnable getEventAction(CloseGateData input) {
            return () -> {
                if (!input.isOtherworldEncounter()) {
                    return;
                }
                ServicePlatform.get().getGameService().showCard(new ShowCardRequestBuilder(getArtifactInfo())
                        .withTitle("Gain one Clue.")
                        .build())
                        .subscribe(showCardResponse -> onAnswer(showCardResponse, input));
            };
        }

        private void onAnswer(ShowCardResponse response, CloseGateData input) {
            ServicePlatform.get().getService().hold();
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
