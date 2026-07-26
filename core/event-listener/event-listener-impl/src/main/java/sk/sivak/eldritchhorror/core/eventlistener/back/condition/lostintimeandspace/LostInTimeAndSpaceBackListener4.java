package sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

public class LostInTimeAndSpaceBackListener4 extends AbstractConditionBackListener {

    public LostInTimeAndSpaceBackListener4(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        if (!ServicePlatform.get().getConditionsDeck().hasCondition(getActiveInvestigatorId(), ConditionId.DARK_PACT)) {
            TypewriterUtils.noYesQuestion("[#BAD]Gain a Dark Pact Condition[] to\n" +
                    "[#GOOD]Place your Investigator\non a space of your choice[]\n" +
                    "and [#GOOD]discard this card?[]", this::onFail, this::onSuccess);
        } else {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("Cannot gain another Dark Pact Condition.\n ");
            onFail();
            ServicePlatform.get().getService().release();
        }

    }

    private void onSuccess() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getGameService().gainCondition(ConditionId.DARK_PACT);
        ServicePlatform.get().getInvestigatorService().hideLostInTimeAndSpace(getActiveInvestigatorId());
        ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(null)).subscribe(this::onSelectLocation);
        ServicePlatform.get().getEncounterService().release();
    }

    private void onFail() {
        TypewriterUtils.confirmInfos(
                "[#BAD]One Gate is spawned[]",
                "Place your Investigator on that space",
                "[#BAD]Omen advances[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().hideLostInTimeAndSpace(getActiveInvestigatorId());
            ServicePlatform.get().getGameService().spawnGates(1, 1).subscribe(gateSpawnData -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().spawnInvestigator(getActiveInvestigatorId(), gateSpawnData.get(0).getGate().getLocationId());
                ServicePlatform.get().getDoomOmenService().advanceOmen();
                discardThisCard();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }


    private void onSelectLocation(LocationId locationId) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().spawnInvestigator(getActiveInvestigatorId(), locationId);
        discardThisCard();
        ServicePlatform.get().getService().release();
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Lost in Time and Space Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
            ServicePlatform.get().getService().release();
        });
    }
}
