package sk.sivak.eldritchhorror.core.eventtype.data.combat;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

public class CombatData {
    private Stat damageTestType;
    private TestFlavorType damageTestFlavorType;
    private MonsterInfo monsterInfo;
    private TestData horrorTestResult;
    private TestData damageTestResult;

    private Integer actualHorror;
    private Integer actualDamage;
    private int sanityLost;
    private int healthLost;
    private int damageDealt;

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    public void setMonsterInfo(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
        setActualHorror(monsterInfo.getHorror());
        setActualDamage(monsterInfo.getDamage());
    }

    public Stat getDamageTestType() {
        return damageTestType;
    }

    public void setDamageTestType(Stat damageTestType) {
        this.damageTestType = damageTestType;
    }

    public TestFlavorType getDamageTestFlavorType() {
        return damageTestFlavorType;
    }

    public void setDamageTestFlavorType(TestFlavorType damageTestFlavorType) {
        this.damageTestFlavorType = damageTestFlavorType;
    }

    public TestData getHorrorTestResult() {
        return horrorTestResult;
    }

    public void setHorrorTestResult(TestData horrorTestResult) {
        this.horrorTestResult = horrorTestResult;
    }

    public TestData getDamageTestResult() {
        return damageTestResult;
    }

    public void setDamageTestResult(TestData damageTestResult) {
        this.damageTestResult = damageTestResult;
    }

    public int getSanityLost() {
        return sanityLost;
    }

    public void setSanityLost(int sanityLost) {
        this.sanityLost = sanityLost;
    }

    public int getHealthLost() {
        return healthLost;
    }

    public void setHealthLost(int healthLost) {
        this.healthLost = healthLost;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(int damageDealt) {
        this.damageDealt = damageDealt;
    }

    public Integer getActualHorror() {
        return actualHorror;
    }

    public void setActualHorror(Integer actualHorror) {
        this.actualHorror = actualHorror;
    }

    public Integer getActualDamage() {
        return actualDamage;
    }

    public void setActualDamage(Integer actualDamage) {
        this.actualDamage = actualDamage;
    }
}
