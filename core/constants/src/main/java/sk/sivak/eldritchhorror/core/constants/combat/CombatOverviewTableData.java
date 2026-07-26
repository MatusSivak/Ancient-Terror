package sk.sivak.eldritchhorror.core.constants.combat;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class CombatOverviewTableData {
    private InvestigatorId investigatorId;
    private StatRowData horrorRowData;
    private StatRowData damageRowData;

    private MonsterCombatTableData monsterCombatTableData = new MonsterCombatTableData();


    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public StatRowData getHorrorRowData() {
        return horrorRowData;
    }

    public void setHorrorRowData(StatRowData horrorRowData) {
        this.horrorRowData = horrorRowData;
    }

    public StatRowData getDamageRowData() {
        return damageRowData;
    }

    public void setDamageRowData(StatRowData damageRowData) {
        this.damageRowData = damageRowData;
    }

    public boolean isMonsterEpic() {
        return monsterCombatTableData.isMonsterEpic();
    }

    public void setMonsterEpic(boolean monsterEpic) {
        monsterCombatTableData.setMonsterEpic(monsterEpic);
    }

    public String getMonsterClassName() {
        return monsterCombatTableData.getMonsterClassName();
    }

    public void setMonsterClassName(String monsterClassName) {
        monsterCombatTableData.setMonsterClassName(monsterClassName);
    }

    public String getMonsterName() {
        return monsterCombatTableData.getMonsterName();
    }

    public void setMonsterName(String monsterName) {
        monsterCombatTableData.setMonsterName(monsterName);
    }

    public Integer getHorror() {
        return monsterCombatTableData.getHorror();
    }

    public void setHorror(Integer horror) {
        monsterCombatTableData.setHorror(horror);
    }

    public Integer getDamage() {
        return monsterCombatTableData.getDamage();
    }

    public void setDamage(Integer damage) {
        monsterCombatTableData.setDamage(damage);
    }

    public Integer getCurrentHealth() {
        return monsterCombatTableData.getCurrentHealth();
    }

    public void setCurrentHealth(Integer currentHealth) {
        monsterCombatTableData.setCurrentHealth(currentHealth);
    }

    public Integer getToughness() {
        return monsterCombatTableData.getToughness();
    }

    public void setToughness(Integer toughness) {
        monsterCombatTableData.setToughness(toughness);
    }

    public static class StatRowData {
        private final Stat stat;
        private final int base;
        private int bonus;
        private int modifier;

        public StatRowData(Stat stat, int base) {
            this.stat = stat;
            this.base = base;
        }


        public Stat getStat() {
            return stat;
        }

        public int getBase() {
            return base;
        }

        public int getBonus() {
            return bonus;
        }

        public void setBonus(int bonus) {
            this.bonus = bonus;
        }

        public int getModifier() {
            return modifier;
        }

        public void setModifier(int modifier) {
            this.modifier = modifier;
        }
    }
}