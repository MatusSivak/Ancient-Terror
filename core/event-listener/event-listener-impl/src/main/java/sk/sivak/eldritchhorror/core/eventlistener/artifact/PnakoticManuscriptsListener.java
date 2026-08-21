package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.PnakoticManuscriptsArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.List;

public class PnakoticManuscriptsListener extends AbstractArtifactListener<PnakoticManuscriptsArtifact> {

    private static final Logger logger = LogManager.getLogger(PnakoticManuscriptsListener.class);

    private RegisterUsableAssetListener registerUsableAssetListener;
    private ActionListener actionListener;
    private EnableCardListener enableCardListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, actionListener, enableCardListener);
    }

    @Override
    protected void register() {
        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);

        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);

        enableCardListener = new EnableCardListener(getArtifactInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class RegisterUsableAssetListener extends AbstractArtifactEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(PnakoticManuscriptsListener.this);
        }

        boolean disable = false;

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.LORE && input.getStat() != Stat.WILL) {
                    return;
                }
                if (getArtifactInfo().isDisabled()) {
                    getArtifactInfo().enable();
                    disable = true;
                }

                addUsableArtifact(input, getArtifactInfo(), 1);
                ServicePlatform.get().getEventQueue().addDirectEventListener(new EventListener<TestData>() {

                    @Override
                    public void onNotify(TestData eventData) {
                        if (disable) {
                            getArtifactInfo().disable();
                        }
                        ServicePlatform.get().getEventQueue().unregisterListener(this);
                    }

                    @Override
                    public Class<TestData> getDataClass() {
                        return TestData.class;
                    }
                }, DirectEvent.REGISTER_BONUS_DICE);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.PnakoticManuscriptsAction> {

        public ActionListener() {
            name = "Pnakotic\nManuscripts";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;

        }

        @Override
        protected String getGeneralDescription() {
            return "If you are on a space\n" +
                    "containing a Gate,\n" +
                    "gain one Clue.";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/PNAKOTIC_MANUSCRIPTS.jpg";
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
        protected PnakoticManuscriptsAction createAction() {
            return new PnakoticManuscriptsAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            if (!ServicePlatform.get().getGateStackRead().isGateAtLocation(getActiveInvestigator().getCurrentLocationId())) {
                disabledReason = "There is no Gate.";
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

        protected class PnakoticManuscriptsAction extends AbstractActionPhaseAction {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Gain one Clue.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().disableCard(getArtifactInfo());
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                    ServicePlatform.get().getTokenService().convertToNull();
                    ServicePlatform.get().getService().release();

                });
            }
        }
    }
}
