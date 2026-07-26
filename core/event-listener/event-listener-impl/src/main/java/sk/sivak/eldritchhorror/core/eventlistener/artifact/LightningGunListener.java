package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.LightningGunArtifact;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
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

public class LightningGunListener extends AbstractArtifactListener<LightningGunArtifact> {

    private static final Logger logger = LogManager.getLogger(LightningGunListener.class);
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
            super(LightningGunListener.this);
        }

        boolean disable = false;
        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                if (getArtifactInfo().isDisabled()) {
                    getArtifactInfo().enable();
                    disable = true;
                }

                addUsableArtifact(input, getArtifactInfo(), 6);
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

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.LightningGunAction> {

        public ActionListener() {
            name = "Lightning\nGun";
        }

        @Override
        protected String getAdditionalInfo() {
            if (isDisabled()) {
                return null;
            }
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getActiveInvestigator().getCurrentLocationId());
            StringBuilder sb = new StringBuilder();
            for (MonsterInfo monsterAtLocation : monstersAtLocation) {
                sb
                        .append(monsterAtLocation.getName())
                        .append(" (")
                        .append(monsterAtLocation.getCurrentHealth())
                        .append(")")
                        .append("\n");
            }
            return sb.toString();
        }

        @Override
        protected String getGeneralDescription() {
            return "You and each Monster on your space lose 1 Health";
        }

        @Override
        protected String getTexturePath() {
            return "card/artifact/LIGHTNING_GUN.jpg";
        }

        @Override
        protected boolean getNeedsScaleDown() {
            return true;
        }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected LightningGunAction createAction() {
            return new LightningGunAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getActiveInvestigator().getCurrentLocationId());
            if (monstersAtLocation == null || monstersAtLocation.isEmpty()) {
                disabledReason = "There are no Monsters here.";
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

        protected class LightningGunAction extends AbstractActionPhaseAction {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setArtifactInfo(getArtifactInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Use Lightning Gun.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    selectMonster();
                });
            }

            private void selectMonster() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getArtifactInfo());
                LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
                ServicePlatform.get().getTokenService().loseHealth(1);

                List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);
                for (MonsterInfo monsterInfo : monstersAtLocation) {
                    ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo,1);
                }
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }



        }
    }
}
