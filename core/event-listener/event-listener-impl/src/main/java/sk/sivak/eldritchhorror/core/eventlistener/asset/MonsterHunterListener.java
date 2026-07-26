package sk.sivak.eldritchhorror.core.eventlistener.asset;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.Dot38RevolverAsset;
import sk.sivak.eldritchhorror.core.constants.asset.MonsterHunterAsset;
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
import sk.sivak.eldritchhorror.core.eventlistener.artifact.LightningGunListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class MonsterHunterListener extends AbstractAssetListener<MonsterHunterAsset> {

    private static final Logger logger = LogManager.getLogger(MonsterHunterListener.class);
    private RegisterUsableAssetListener registerUsableAssetListener;
    private ActionListener actionListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        registerUsableAssetListener = new RegisterUsableAssetListener();
        getEventQueue().addDirectEventListener(registerUsableAssetListener, DirectEvent.REGISTER_USABLE_ASSETS);

        actionListener = new ActionListener();
        getEventQueue().addBeforeEventListener(actionListener, BeforeAfterEvent.FIND_ACTIONS);

        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(registerUsableAssetListener, actionListener, enableCardListener);
    }

    private class RegisterUsableAssetListener extends AbstractAssetEventListener<TestData> {

        public RegisterUsableAssetListener() {
            super(MonsterHunterListener.this);
        }

        @Override
        protected Runnable getEventAction(TestData input) {
            return () -> {
                if (input.getStat() != Stat.STRENGTH) {
                    return;
                }
                if (!isTestFlavorOfType(TestFlavorType.COMBAT)) {
                    return;
                }
                addUsableAsset(input, getAssetInfo(), 2);
            };
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class ActionListener extends AbstractActionPhaseListener<ActionListener.MonsterHunterAction> {

        public ActionListener() {
            name = "Monster\nHunter";
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
            return "A Monster of your choice\n" +
                    "on your space\n" +
                    "loses one Health";
        }

        @Override
        protected String getTexturePath() {
            return "card/asset/MONSTER_HUNTER.jpg";
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
        protected MonsterHunterAction createAction() {
            return new MonsterHunterAction();
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

        protected class MonsterHunterAction extends AbstractActionPhaseAction {

            @Override
            public void execute() {
                ShowCardRequest showCardRequest = new ShowCardRequest();
                showCardRequest.setAssetInfo(getAssetInfo());
                showCardRequest.setHideType(HideType.DISABLE_ALWAYS);
                showCardRequest.setTitle("Hunt monster.");
                showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
                Single<ShowCardResponse> displayAsset = ServicePlatform.get().getGameService().showCard(showCardRequest);
                displayAsset.subscribe(response -> {
                    selectMonster();
                });
            }

            private void selectMonster() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getAssetInfo());
                LocationId currentLocationId = getActiveInvestigator().getCurrentLocationId();
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
