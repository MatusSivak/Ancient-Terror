package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Tokyo13Encounter extends AbstractLocationEncounter{

    public Tokyo13Encounter() {
        super(13, LocationEncounter.LocationEncounterType.TOKYO);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -2,
                this::selectSpaceWithMonsters,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.POISONED)
        ).execute();
    }

    private void selectSpaceWithMonsters() {
        if (ServicePlatform.get().getMonsterCup().getMonsters().isEmpty()) {
            Question<Object> question = new Question<>();
            question.setTitle("There are no Monsters.");
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", true)));
            ServicePlatform.get().getGameService().ask(question).subscribe(response -> {});
            return;
        }
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        List<LocationId> locations = new LinkedList<>(Stream.map(monsters, MonsterInfo::getCurrentLocation));
        ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(locations)).subscribe(this::attackMonsters);
    }

    private void attackMonsters(LocationId locationId) {
        List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(locationId);
        ServicePlatform.get().getService().hold();
        for (MonsterInfo monsterInfo : monstersAtLocation) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
            ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 2);
            ServicePlatform.get().getService().release();
        }
        ServicePlatform.get().getService().release();
    }

}
