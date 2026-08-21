package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.LivreDIvonArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class LivreDIvonListener extends AbstractArtifactListener<LivreDIvonArtifact> {

    private static final Logger logger = LogManager.getLogger(LivreDIvonListener.class);
    private ActionListener actionListener;
    private InitActionButtonListener initActionButtonListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);

        initActionButtonListener = new InitActionButtonListener();
        getEventQueue().addDirectEventListener(initActionButtonListener, DirectEvent.INIT_ACTION_BUTTON);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class InitActionButtonListener extends EventListenerImpl<Void> {
        @Override
        public void onNotify(Void eventData) {
            actionListener.init();
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }

        @Override
        public String toString() {
            return "LivreDIvonListener - InitActionButton";
        }
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(actionListener, initActionButtonListener, enableCardListener);
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.LivreDIvonAction> {

        private boolean enoughSanity;

        private void init() {
            if (!isVisible()) {
                return;
            }
            ServicePlatform.get().getTokenService().canSpend(0,0,0,1).subscribe(spendData -> {
                enoughSanity = spendData.hasEnough();
            });
        }

        public ActionListener() {
            name = "Livre\n" +
                    "d'Ivon";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return "Test Lore-1. If you pass,\n" +
                    "you may spend one Sanity\n" +
                    "to move to any space.";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/LIVRE_D_IVON.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
        return 1.0f;
    }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected LivreDIvonAction createAction() {
            return new LivreDIvonAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            if (!enoughSanity) {
                disabledReason = "You do not have enough Sanity.";
                return true;
            }
            if (!enabled) {
                disabledReason = "Action already performed this round"; // THIS MUST BE LAST
                return true;
            }
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }

        protected class LivreDIvonAction extends AbstractActionPhaseAction {

            @Override
            public void execute() {
                ServicePlatform.get().getTokenService().canSpend(0,0,0,1).subscribe(this::onCanSpend);
            }

            private void onCanSpend(SpendData spendData) {
                if (!spendData.hasEnough()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Spend Sanity to move to any space.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    testLore();
                });
            }

            private void testLore() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                ServicePlatform.get().getTestService().test(Stat.LORE, -1, JUST_ONE).subscribe(this::afterTest);
                ServicePlatform.get().getService().release();
            }

            private void afterTest(TestData testData) {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ServicePlatform.get().getTokenService().spend(0,0,0,1).subscribe(this::onSpend);
            }

            private void onSpend(SpendData spendData) {
                if (!spendData.hasEnough()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ServicePlatform.get().getService().hold();
                spendData.pay();
                ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(null)).subscribe(locationId -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
                        @Override
                        public LocationId getLocationId() {
                            return locationId;
                        }

                        @Override
                        public PathType getPathType() {
                            return PathType.WALK;
                        }
                    });
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getService().release();
            }
        }


    }
}
