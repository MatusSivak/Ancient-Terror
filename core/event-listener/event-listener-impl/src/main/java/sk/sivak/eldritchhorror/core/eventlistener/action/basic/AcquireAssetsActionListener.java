package sk.sivak.eldritchhorror.core.eventlistener.action.basic;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.MonsterCupRead;

import java.util.List;

/**
 * @author msivak
 */
public class AcquireAssetsActionListener extends AbstractActionPhaseListener<AcquireAssetsActionListener.AcquireAssetsAction> {

    private static final Logger logger = LogManager.getLogger(AcquireAssetsActionListener.class);

    public AcquireAssetsActionListener() {
        name = "Acquire\nAssets";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
            return null;
        }
        List<AssetInfo> reserve = ServicePlatform.get().getAssetDeck().getReserve();

        StringBuilder sb = new StringBuilder();
        for (AssetInfo assetInfo : reserve) {
            sb.append(assetInfo.getName()).append(" (").append(assetInfo.getCost()).append(")").append("\n");
        }
        return sb.toString();

    }

    @Override
    protected String getGeneralDescription() {
        return "Test Influence.\n" +
                "Gain Assets of your choice from the reserve\n" +
                "with total value equal to or less than the test result";
    }

    @Override
    protected AcquireAssetsAction createAction() {
        return new AcquireAssetsAction();
    }

    @Override
    protected boolean isVisible() {
        return true;
    }

    @Override
    protected boolean isDisabled() {
        InvestigatorRead activeInvestigator = getInvestigators().getActiveInvestigator();
        LocationInfo locationInfo = getLocationMap().getLocationInfo(activeInvestigator.getCurrentLocationId());
        if (LocationType.CITY != locationInfo.getLocationType()) {
            disabledReason = "Can be only performed in City.";
            return true;
        }
        MonsterCupRead monsterCup = ServicePlatform.get().getMonsterCup();
        if (monsterCup.containsMonster(activeInvestigator.getCurrentLocationId())) {
            disabledReason = "Cannot acquire assets on space containing monsters.";
            return true;
        }
        if (ServicePlatform.get().getAssetDeck().getReserve().isEmpty()) {
            disabledReason = "Reserve is empty.";
            return true;
        }
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.ACQUIRE_ASSETS;
    }

    @Override
    protected String getTexturePath() {
        return "action_button/acquire_assets.png";
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class AcquireAssetsAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {

            logger.info("Executing acquire assets action...");
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().displayText("Acquire Assets");
            int totalValue = 0;
            for (AssetInfo assetInfo : ServicePlatform.get().getAssetDeck().getReserve()) {
                totalValue += assetInfo.getCost();
            }
            Single<TestData> test = ServicePlatform.get().getTestService().test(
                    Stat.INFLUENCE, 0, totalValue, new TestFlavorRequest(TestFlavorType.ACQUIRE_ASSETS));
            test.subscribe(this::withTestData, onError -> logger.error("ERROR : ", onError));
            ServicePlatform.get().getService().release();

        }

        private void withTestData(TestData testData) {
                ServicePlatform.get().getCardService().hold();
                ServicePlatform.get().getCardService().acquireAssets(testData.getScore());
                ServicePlatform.get().getTestService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getCardService().release();

        }
    }

}
