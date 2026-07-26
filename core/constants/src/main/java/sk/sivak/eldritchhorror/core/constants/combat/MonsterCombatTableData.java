package sk.sivak.eldritchhorror.core.constants.combat;

import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;

public class MonsterCombatTableData {

    private boolean isMonsterEpic;
    private String monsterClassName;
    private String monsterName;
    private Integer horror;
    private Integer damage;
    private Integer currentHealth;
    private Integer toughness;

    private MonsterInfo monsterInfo;

    public MonsterCombatTableData() {
    }

    public void setMonsterInfo(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    public boolean isMonsterEpic() {
        return isMonsterEpic;
    }

    public void setMonsterEpic(boolean monsterEpic) {
        isMonsterEpic = monsterEpic;
    }

    public String getMonsterClassName() {
        return monsterClassName;
    }

    public void setMonsterClassName(String monsterClassName) {
        this.monsterClassName = monsterClassName;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public Integer getHorror() {
        return horror;
    }

    public void setHorror(Integer horror) {
        this.horror = horror;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public Integer getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(Integer currentHealth) {
        this.currentHealth = currentHealth;
    }

    public Integer getToughness() {
        return toughness;
    }

    public void setToughness(Integer toughness) {
        this.toughness = toughness;
    }
}
