package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendCluePassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

public class Tokyo7Encounter extends AbstractTokyoEncounter{

    public Tokyo7Encounter() {
        super(7);
    }

    @Override
    protected void execute() {
        SpendCluePassFlavorInfoTemplate template = new SpendCluePassFlavorInfoTemplate(getTextBuilder(), this::selectSingleMonster)
                .withOnNoAction(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor().build()))
                .withOnMissingClue(() -> finishAfterFlavor(getTextBuilder().withFail().withFlavor(2).build()));
        if (!hasMonsterTargets(false, Integer.MAX_VALUE)) template.withPassWithoutEffect(2);
        template.execute();

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
