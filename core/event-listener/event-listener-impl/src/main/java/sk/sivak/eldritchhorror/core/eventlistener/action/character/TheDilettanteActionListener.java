package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import java.util.ArrayList;
import java.util.List;

public class TheDilettanteActionListener extends AbstractActionPhaseListener<TheDilettanteActionListener.CharacterAction> {
    public TheDilettanteActionListener() { name = "Shopping\nSpree"; }
    @Override protected String getAdditionalInfo() { return null; }
    @Override protected String getGeneralDescription() { return getActiveInvestigator().getInfo().getActionText(); }
    @Override protected CharacterAction createAction() { return new CharacterAction(); }
    @Override protected boolean isVisible() { return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_DILETTANTE; }
    @Override protected boolean isDisabled() {
        if (ServicePlatform.get().getAssetDeck().getReserve().isEmpty()) {
            disabledReason = "Reserve is empty.";
            return true;
        }
        disabledReason = "No investigator is on a City space without monsters.";
        return shoppers().isEmpty();
    }
    @Override protected boolean isNotRecommended() { return false; }
    @Override protected ActionButtonData.ActionButtonId getActionButtonId() { return ActionButtonData.ActionButtonId.INVESTIGATOR; }
    @Override protected String getTexturePath() { return "investigator/THE_DILETTANTE.png"; }

    private List<InvestigatorId> shoppers() {
        List<InvestigatorId> result = new ArrayList<>();
        for (InvestigatorRead investigator : getInvestigators().getOnBoardInvestigators()) {
            if (getLocationMap().getLocationInfo(investigator.getCurrentLocationId()).getLocationType() != LocationType.CITY) continue;
            if (ServicePlatform.get().getMonsterCup().containsMonster(investigator.getCurrentLocationId())) continue;
            result.add(investigator.getInfo().getInvestigatorId());
        }
        return result;
    }

    protected class CharacterAction extends AbstractActionPhaseAction {
        @Override public void execute() {
            List<InvestigatorId> shoppers = shoppers();
            if (shoppers.size() == 1 && shoppers.get(0) == InvestigatorId.THE_DILETTANTE) {
                acquireAssets(InvestigatorId.THE_DILETTANTE);
                return;
            }
            InvestigatorRestriction restriction = new InvestigatorRestriction(true);
            restriction.addAllowedInvestigators(shoppers);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(restriction).subscribe(this::acquireAssets);
        }

        private void acquireAssets(InvestigatorId shopper) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(shopper);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getGameService().displayText("Acquire Assets (+2 Influence)");
            int totalValue = 0;
            for (AssetInfo assetInfo : ServicePlatform.get().getAssetDeck().getReserve()) totalValue += assetInfo.getCost();
            ServicePlatform.get().getTestService().test(Stat.INFLUENCE, 2, totalValue, new TestFlavorRequest(TestFlavorType.ACQUIRE_ASSETS))
                    .subscribe(testData -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getCardService().acquireAssets(testData.getScore());
                        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_DILETTANTE);
                        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                        ServicePlatform.get().getService().convertToNull();
                        ServicePlatform.get().getService().release();
                    });
            ServicePlatform.get().getService().release();
        }
    }
}
