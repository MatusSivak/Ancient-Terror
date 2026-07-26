package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.List;

public class ResearchSea2Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchSea2Encounter() {
        super(2, LocationType.SEA);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -1,
                () -> {
                    ServicePlatform.get().getService().hold();
                    gainThisClue();
                    selectSingleMonster();
                    ServicePlatform.get().getService().release();
                }
        ).withResearchFlavor().withTwoPassInfos().execute();
    }

    private void selectSingleMonster() {
        SelectMonsterData selectMonsterData = new SelectMonsterData();
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        selectMonsterData.setAvailableMonsters(Stream.collectToList(monsters, monsterInfo -> !monsterInfo.isEpic()));
        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
    }

    private void onMonsterSelect(MonsterInfo monsterInfo) {
        if (monsterInfo == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
        ServicePlatform.get().getService().release();
    }
}
