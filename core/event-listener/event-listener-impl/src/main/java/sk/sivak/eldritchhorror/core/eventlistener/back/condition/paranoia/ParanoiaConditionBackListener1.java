package sk.sivak.eldritchhorror.core.eventlistener.back.condition.paranoia;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class ParanoiaConditionBackListener1 extends AbstractConditionBackListener {

    public ParanoiaConditionBackListener1(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.RETURN);
        request.setConditionInfo(getConditionInfo());
        request.setTitle("Test Will");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        ServicePlatform.get().getGameService().showCard(request).subscribe(this::onTest);
    }

    private void onTest(ShowCardResponse showCardResponse) {
        ServicePlatform.get().getTestService().test(Stat.WILL, 0, 1,
                new TestFlavorRequest(TestFlavorType.CONDITION, getConditionInfo())).subscribe(testResult -> {
            if (!testResult.isSuccessful()) {
                onFlip(null);
            }
        });
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                "[#BAD]Each other investigator\non your space loses two Health[]",
                "[#BAD]Discard one Ally[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
                if (investigator.getInfo().getInvestigatorId() == activeInvestigatorId) {
                    continue;
                }
                if (investigator.getCurrentLocationId() != ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId()) {
                    continue;
                }
                ServicePlatform.get().getService().addEventCommand(whatever -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    ServicePlatform.get().getTokenService().loseHealth(2);
                    ServicePlatform.get().getService().release();
                });
            }
            ServicePlatform.get().getService().addEventCommand(whatever -> {
                if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() == activeInvestigatorId) {
                    return;
                }
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getService().release();
            });

            ServicePlatform.get().getService().addEventCommand(whatever -> {
                List<? extends CardInfo> allies = findAllies();
                if (!allies.isEmpty()) {
                    selectAllyToDiscard(allies);
                }
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void selectAllyToDiscard(List<? extends CardInfo> investigatorAllies) {
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorAllies);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Ally");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }

    private List<? extends CardInfo> findAllies() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ALLY);
    }

}
