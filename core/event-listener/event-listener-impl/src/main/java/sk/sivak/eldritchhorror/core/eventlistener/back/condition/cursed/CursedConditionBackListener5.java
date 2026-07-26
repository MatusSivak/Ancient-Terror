package sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class CursedConditionBackListener5 extends AbstractCursedConditionBackListener {

    public CursedConditionBackListener5(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        LocationInfo locationInfo = ServicePlatform.get().getLocationMap().getLocationInfo(currentLocationId);

        if (locationInfo.getLocationType() == LocationType.SEA) {
            ServicePlatform.get().getEncounterService().typeFlavor("Your body begins to change horribly into a fish-like creature.");
            TypewriterUtils.confirmInfos("[#BAD]You are Devoured[]").subscribe(() -> {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getInvestigatorService().devourInvestigator(getActiveInvestigatorId());
                ServicePlatform.get().getEncounterService().release();
            });
        } else {
            ServicePlatform.get().getEncounterService().typeFlavor("Your body begins to change horribly into a fish-like creature. The change cripples you.");
            TypewriterUtils.confirmInfos(
                    "[#BAD]Lose three Health[]",
                    "[#GOOD]Discard this card[]").subscribe(() -> {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().loseHealth(3);
                discardThisCard();
                ServicePlatform.get().getEncounterService().release();
            });
        }
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Cursed Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
