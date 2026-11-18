package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

public class London14Encounter extends AbstractLocationEncounter{

    public London14Encounter() {
        super(14, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        boolean hasTarget = false;
        for (LocationId location : ServicePlatform.get().getCluePool().getClueLocations()) {
            if (ServicePlatform.get().getMonsterCup().containsMonster(location)) hasTarget = true;
        }
        if (!hasTarget) {
            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                    () -> {}, () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED))
                    .withPassFlavorVariant(2).withOkAfterPassFlavor().execute();
            return;
        }
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::eachMonsterOnClueLosesOneHealth,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED)
        ).withOutcomeFlavors().execute();
    }

    private void eachMonsterOnClueLosesOneHealth() {
        for (LocationId clueLocation : ServicePlatform.get().getCluePool().getClueLocations()) {
            if (!ServicePlatform.get().getMonsterCup().containsMonster(clueLocation)) {
                continue;
            }
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(clueLocation);
            for (MonsterInfo monsterInfo : monstersAtLocation) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().moveCameraToLocation(clueLocation);
                ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 1);
                ServicePlatform.get().getService().release();
            }
        }
    }
}
