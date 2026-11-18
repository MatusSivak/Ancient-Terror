package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

public class Tokyo1Encounter extends AbstractTokyoEncounter{

    public Tokyo1Encounter() {
        super(1);
    }

    @Override
    protected void execute() {
        if (!hasMonsterTargets(false, Integer.MAX_VALUE)) {
            finishAfterFlavor(getTextBuilder().withPass().withFlavor(2).build());
            return;
        }
        TypewriterUtils.confirmInfos(getTextBuilder().withInfo().build()).subscribe(() -> {
            ServicePlatform.get().getEncounterService().hold();
            selectSingleMonster();
            ServicePlatform.get().getEncounterService().release();
        });
    }

    private void selectSingleMonster() {
        SelectMonsterData selectMonsterData = new SelectMonsterData();
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        monsters = Stream.collectToList(monsters, monsterInfo -> !monsterInfo.isEpic());
        selectMonsterData.setAvailableMonsters(monsters);
        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
    }

    private void onMonsterSelect(MonsterInfo monsterInfo) {
        if (monsterInfo == null) {
            finishAfterFlavor(getTextBuilder().withPass().withFlavor(2).build());
            return;
        }
        finishAfterFlavor(getTextBuilder().withPass().withFlavor().build(), () -> {
            ServicePlatform.get().getMonsterService().moveMonster(monsterInfo, ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
            ServicePlatform.get().getTestService().combat(monsterInfo);
        });
    }
}
