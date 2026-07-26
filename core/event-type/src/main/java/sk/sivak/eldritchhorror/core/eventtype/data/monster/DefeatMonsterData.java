package sk.sivak.eldritchhorror.core.eventtype.data.monster;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class DefeatMonsterData {
    private MonsterInfo monsterInfo;
    private boolean inCombat;
    private boolean destroyCard;

    public DefeatMonsterData(MonsterInfo monsterInfo, boolean inCombat) {
        this.monsterInfo = monsterInfo;
        this.inCombat = inCombat;
    }

    public void setDestroyCard(boolean destroyCard) {
        this.destroyCard = destroyCard;
    }

    public boolean isDestroyCard() {
        return destroyCard;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    @Override
    public String toString() {
        return "DefeatMonsterData{" +
                "monsterInfo=" + monsterInfo +
                ", inCombat=" + inCombat +
                '}';
    }
}
