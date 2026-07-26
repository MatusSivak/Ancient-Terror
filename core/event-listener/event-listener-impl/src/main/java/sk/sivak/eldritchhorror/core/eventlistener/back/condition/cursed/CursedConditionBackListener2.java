package sk.sivak.eldritchhorror.core.eventlistener.back.condition.cursed;

import java8.features.function.Predicate;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.monster.epic.EpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

public class CursedConditionBackListener2 extends AbstractCursedConditionBackListener {

    public CursedConditionBackListener2(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {

        TypewriterUtils.confirmInfos("[#BAD]Spawn the Doppelganger Epic Monster\non the nearest Gate[]").subscribe(() -> {
            if (ServicePlatform.get().getGateStackRead().getSpawnedGatesLocations().isEmpty()) {
                TypewriterUtils.confirmInfos("There are no spawned Gates.").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
                return;
            }

            if (!ServicePlatform.get().getMonsterCup().isEpicMonsterPresent(EpicMonsterId.DOPPELGANGER)) {
                TypewriterUtils.confirmInfos("Doppelganger already spawned.").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
                return;
            }

            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
            Predicate<LocationInfo> isGateAtThisLocationPredicate = locationInfo -> ServicePlatform.get().getGateStackRead().isGateAtLocation(locationInfo.getLocationId());
            FindNearestData nearestGate = ServicePlatform.get().getLocationMap().findNearest(currentLocationId, isGateAtThisLocationPredicate);


            if (nearestGate.getLocationId() == currentLocationId) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                spawnDoppelganger(nearestGate);
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                TypewriterUtils.confirmInfos("[#BAD]You are Devoured[]").subscribe(() -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    ServicePlatform.get().getInvestigatorService().devourInvestigator(getActiveInvestigatorId());
                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                spawnDoppelganger(nearestGate);
                ServicePlatform.get().getService().moveCameraToLocation(currentLocationId);
                ServicePlatform.get().getService().release();
            }
        });
    }

    private void spawnDoppelganger(FindNearestData nearestGate) {
        SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
        spawnMonsterData.setLocationId(nearestGate.getLocationId());
        spawnMonsterData.setMonsterId(EpicMonsterId.DOPPELGANGER);
        ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
    }
}
