package sk.sivak.eldritchhorror.core.eventlistener.back.condition.lostintimeandspace;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.Encounter;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.OtherWorldEncounter;

public class LostInTimeAndSpaceBackListener3 extends AbstractConditionBackListener {

    public LostInTimeAndSpaceBackListener3(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestButton(Stat.INFLUENCE, -1, this::onSuccess, this::onFail);
    }

    private void onSuccess() {
        TypewriterUtils.confirmInfos(
                "[#GOOD]Place your Investigator\non a space of your choice.[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().hideLostInTimeAndSpace(getActiveInvestigatorId());
            ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(null)).subscribe(this::onSelectLocation);
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void onFail() {
        TypewriterUtils.confirmInfos(
                "[#BAD]One Gate is spawned[]",
                "[#BAD]Spawn the Doppelganger Epic Monster\non that space[]",
                "Place your Investigator on a random space.",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getInvestigatorService().hideLostInTimeAndSpace(getActiveInvestigatorId());
            ServicePlatform.get().getGameService().spawnGates(1, 1).subscribe(gateSpawnData -> {
                ServicePlatform.get().getService().hold();
                GateInfo gate = gateSpawnData.get(0).getGate();

                SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                spawnMonsterData.setLocationId(gate.getLocationId());
                spawnMonsterData.setMonsterId(EpicMonsterId.DOPPELGANGER);
                ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);

                onSelectLocation(LocationId.getRandomLocations(1).get(0));
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getEncounterService().release();
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
