package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class GreenMythosCard3 implements MythosCardEventListener{

    @Override
    public void execute() {
        InvestigatorRead leadInvestigator = ServicePlatform.get().getInvestigators().getLeadInvestigator();
        if (leadInvestigator == null) {
            return;
        }
        List<MonsterInfo> monsters = ServicePlatform.get().getMonsterCup().getMonsters();
        SelectMonsterData selectMonsterData = new SelectMonsterData();
        selectMonsterData.setAvailableMonsters(Stream.collectToList(monsters,
                monsterInfo -> !monsterInfo.isEpic()));
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(leadInvestigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(selectedMonster -> {
            if (selectedMonster != null) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getMonsterService().discardMonster(selectedMonster);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getTokenService().loseHealth(selectedMonster.getToughness());
                ServicePlatform.get().getService().release();
            }
        });
        ServicePlatform.get().getService().release();
    }
}
