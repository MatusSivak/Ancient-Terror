package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

public class Tokyo12Encounter extends AbstractLocationEncounter{

    public Tokyo12Encounter() {
        super(12, LocationEncounter.LocationEncounterType.TOKYO);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0,
                this::discardWeakMonsters,
                () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED)
        ).execute();
    }

    private void discardWeakMonsters() {
        ServicePlatform.get().getService().hold();
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        monsters = Stream.collectToList(monsters, monsterInfo -> !monsterInfo.isEpic() && monsterInfo.getToughness()==1);
        ServicePlatform.get().getService().hideBackground();
        for (MonsterInfo monster : monsters) {
            ServicePlatform.get().getMonsterService().discardMonster(monster);
        }
        ServicePlatform.get().getService().release();
    }
}
