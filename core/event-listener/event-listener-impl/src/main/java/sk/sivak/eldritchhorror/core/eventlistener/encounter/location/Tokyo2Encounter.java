package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.BecomeDelayedPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

import java.util.List;

public class Tokyo2Encounter extends AbstractTokyoEncounter{

    public Tokyo2Encounter() {
        super(2);
    }

    @Override
    protected void execute() {
        BecomeDelayedPassFlavorInfoTemplate template = new BecomeDelayedPassFlavorInfoTemplate(getTextBuilder(), this::selectSingleMonster).withAnswerFlavors();
        if (!hasMonsterTargets(true, Integer.MAX_VALUE)) template.withPassWithoutEffect(3);
        template.execute();
    }

    private void selectSingleMonster() {
        SelectMonsterData selectMonsterData = new SelectMonsterData();
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        selectMonsterData.setAvailableMonsters(monsters);
        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(this::onMonsterSelect);
    }

    private void onMonsterSelect(MonsterInfo monsterInfo) {
        if (monsterInfo == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().moveCameraToLocation(monsterInfo.getCurrentLocation());
        ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 3);
        ServicePlatform.get().getService().release();
    }
}
