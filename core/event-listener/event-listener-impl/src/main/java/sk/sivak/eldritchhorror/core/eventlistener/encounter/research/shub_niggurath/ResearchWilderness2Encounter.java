package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

import java.util.List;

public class ResearchWilderness2Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness2Encounter() {
        super(2, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1, () -> {
            gainThisClue();
        }, () -> {
            SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
            spawnMonsterData.setLocationId(getLocationId());
            if (ServicePlatform.get().getMonsterCup().canSpawn(NonEpicMonsterId.DARK_YOUNG)) {
                spawnMonsterData.setMonsterId(NonEpicMonsterId.DARK_YOUNG);
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
            ServicePlatform.get().getService().<SpawnMonsterData>addEventCommand( smd -> {
                ServicePlatform.get().getTestService().combat(smd.getMonsterInfo());
            });
            ServicePlatform.get().getService().release();
        }).withResearchFlavor().execute();
    }
}
