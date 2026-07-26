package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class GreenMythosCard5 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        Collection<LocationId> locationIds = Stream.map(Stream.collectToList(monsters,
                monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.CULTIST),
                MonsterInfo::getCurrentLocation);

        HashSet<LocationId> uniqueLocations = new HashSet<>(locationIds);
        if (uniqueLocations.isEmpty()) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("There are no Cultists.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        }
        for (LocationId locationId : uniqueLocations) {
            SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
            spawnMonsterData.setLocationId(locationId);
            ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
        }

    }
}
