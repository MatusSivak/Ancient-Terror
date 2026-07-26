package sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

public class DebtConditionBackListener1 extends AbstractConditionBackListener{

    public DebtConditionBackListener1(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(canSpendData -> {
            if (canSpendData.hasEnough()) {
                TypewriterUtils.noYesQuestion("[#BAD]Spend one Clue?", this::notSpendingClue, this::spendingClue);
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().typeInfo("You don't have a Clue.\n ");
                notSpendingClue();
                ServicePlatform.get().getService().release();
            }
        });
    }

    private void spendingClue() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spendData -> {
            ServicePlatform.get().getService().hold();
            if (spendData.hasEnough()) {
                spendData.pay();
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                TypewriterUtils.confirmInfos("[#GOOD]Discard this card[]").subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    discardThisCard();
                    ServicePlatform.get().getService().release();
                });
            } else {
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                notSpendingClue();
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    private void notSpendingClue() {
        TypewriterUtils.confirmInfos(
                "[#BAD]Move to a random space[]",
                "[#BAD]Become Delayed[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            LocationId locationId = LocationId.getRandomLocations(1).get(0);
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
            ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), true));
            discardThisCard();
            ServicePlatform.get().getService().release();
        });
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Debt Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
