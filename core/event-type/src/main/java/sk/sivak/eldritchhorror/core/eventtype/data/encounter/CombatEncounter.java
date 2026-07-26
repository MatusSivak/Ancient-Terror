package sk.sivak.eldritchhorror.core.eventtype.data.encounter;

import sk.sivak.eldritchhorror.core.constants.encounter.CombatEncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterButtonData;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class CombatEncounter extends Encounter {

    private MonsterInfo monsterInfo;

    public CombatEncounter(MonsterInfo monsterInfo) {
        super(EncounterType.COMBAT);
        this.monsterInfo = monsterInfo;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    @Override
    public EncounterButtonData buildButtonData() {
        this.setUuid(monsterInfo.getUuid());
        CombatEncounterButtonData combatEncounterButtonData = new CombatEncounterButtonData(monsterInfo.getUuid(), monsterInfo);
        if (!isEnabled()) {
            combatEncounterButtonData.disable(getDisabledReason());
        }
        String monsterClassSimpleName = monsterInfo.getClass().getSimpleName();
        String monsterImagePath = monsterClassSimpleName.substring(0, monsterClassSimpleName.length() - "Monster".length());
        combatEncounterButtonData.setButtonIcon(monsterInfo.isEpic() ?
                "monster/epic/" + monsterImagePath + ".png" : "monster/" + monsterImagePath + ".png");
        combatEncounterButtonData.setFirstLine(monsterInfo.isEpic() ?
                monsterInfo.getName() + " - EPIC" : monsterInfo.getName());

        return combatEncounterButtonData;
    }
}
