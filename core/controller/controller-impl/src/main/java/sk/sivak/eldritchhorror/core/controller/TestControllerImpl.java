package sk.sivak.eldritchhorror.core.controller;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.combat.CombatOverviewTableData;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorsRead;
import sk.sivak.eldritchhorror.core.view.TestView;

import java.util.List;

/**
 * @author msivak
 */
public class TestControllerImpl implements TestController {

    private TestView testView;

    private InvestigatorsRead investigators;

    public void setInvestigators(InvestigatorsRead investigators) {
        this.investigators = investigators;
    }

    public void setView(TestView testView) {
        this.testView = testView;
    }

    @Override
    public Single<List<UsableAsset>> confirmTest(Stat stat, int modifier, int baseStatValue, int bonusStatValue,
                                                 List<UsableAsset> usableAssets, int additionalDicesCount, boolean isCombat) {
        return testView.confirmTest(stat, modifier, baseStatValue, bonusStatValue, usableAssets, additionalDicesCount, isCombat);
    }

    @Override
    public Completable showRolledTestDices(List<DiceRoll> diceRolls) {
        return testView.showRolledTestDices(diceRolls);
    }

    @Override
    public Completable showRolledDices(List<DiceRoll> diceRolls) {
        return testView.showRolledDices(diceRolls);
    }

    @Override
    public Completable confirmTestResult(boolean scoreImportant, boolean successful, int score) {
        return testView.confirmTestResult(scoreImportant, successful, score);
    }

    @Override
    public Completable confirmRollResult(int score) {
        return testView.confirmRollResult(score);
    }

    @Override
    public Completable rerollDice(List<DiceRoll> diceRoll) {
        return testView.rerollDice(diceRoll);
    }

    @Override
    public Single<Boolean> askRerollUsingFocus(Question question) {
        return testView.askRerollUsingFocus(question);
    }

    @Override
    public Single<Boolean> askRerollUsingClue(Question question) {
        return testView.askRerollUsingClue(question);
    }

    @Override
    public Single<Integer> addOneToDieResult(int minSuccess) {
        return testView.addOneToDieResult(minSuccess);
    }

    @Override
    public Completable showCombatOverview(InvestigatorId investigatorId, MonsterInfo monsterInfo, Stat damageTestType) {
        CombatOverviewTableData data = new CombatOverviewTableData();
        data.setInvestigatorId(investigatorId);
        InvestigatorRead investigator = investigators.getInvestigator(investigatorId);
        if (monsterInfo.getHorror() != null) {
            Stat horrorTestType = monsterInfo.getHorrorTestType();
            CombatOverviewTableData.StatRowData horrorRowData = new CombatOverviewTableData.StatRowData(
                    horrorTestType, investigator.getInfo().getBaseStat(horrorTestType));
            horrorRowData.setModifier(monsterInfo.getHorrorTestModifier());
            horrorRowData.setBonus(investigator.getStatBonus(horrorTestType));
            data.setHorrorRowData(horrorRowData);
        }
        if (monsterInfo.getDamage() != null) {
            CombatOverviewTableData.StatRowData damageRowData = new CombatOverviewTableData.StatRowData(
                    damageTestType, investigator.getInfo().getBaseStat(damageTestType));
            damageRowData.setModifier(monsterInfo.getDamageTestModifier());
            damageRowData.setBonus(investigator.getStatBonus(damageTestType));
            data.setDamageRowData(damageRowData);
        }
        data.setMonsterEpic(monsterInfo.isEpic());
        data.setMonsterClassName(monsterInfo.getClass().getSimpleName());
        data.setMonsterName(monsterInfo.getNameInSelectComponent());
        data.setHorror(monsterInfo.getHorror());
        data.setDamage(monsterInfo.getDamage());
        data.setCurrentHealth(monsterInfo.getCurrentHealth());
        data.setToughness(monsterInfo.getToughness());

        return testView.showCombatOverview(data);
    }


    @Override
    public Completable highlightHorrorCombat() {
        return testView.highlightHorrorCombat();
    }


    @Override
    public Completable highlightDamageCombat() {
        return testView.highlightDamageCombat();
    }

    private MonsterCombatTableData createMonsterCombatTableData(MonsterInfo monsterInfo, Integer actualHorror, Integer actualDamage) {
        MonsterCombatTableData data = new MonsterCombatTableData();
        data.setMonsterEpic(monsterInfo.isEpic());
        data.setMonsterClassName(monsterInfo.getClass().getSimpleName());
        data.setMonsterName(monsterInfo.getNameInSelectComponent());
        data.setMonsterInfo(monsterInfo);
        data.setHorror(actualHorror);
        data.setDamage(actualDamage);
        data.setCurrentHealth(monsterInfo.getCurrentHealth());
        data.setToughness(monsterInfo.getToughness());
        return data;
    }

    @Override
    public Completable destroyHorror(List<DiceRoll> diceRolls) {
        return testView.destroyHorror(diceRolls);
    }

    @Override
    public Completable destroyDamage(List<DiceRoll> diceRolls) {
        return testView.destroyDamage(diceRolls);
    }

    @Override
    public Completable destroySanity(int sanityLost) {
        return testView.destroySanity(sanityLost);
    }

    @Override
    public Completable destroyHealth(int healthLost) {
        return testView.destroyHealth(healthLost);
    }

    @Override
    public Completable showCombatTable(MonsterInfo monsterInfo, Integer actualHorror, Integer actualDamage) {
        return testView.showCombatTable(createMonsterCombatTableData(monsterInfo, actualHorror, actualDamage));
    }

    @Override
    public Completable hideCombatTable() {
        return testView.hideCombatTable();
    }

    @Override
    public Completable destroyMonsterHealth(List<DiceRoll> diceRolls) {
        return testView.destroyMonsterHealth(diceRolls);
    }

    @Override
    public Completable waitForCombatTableCentered() {
        return testView.waitForCombatTableCentered();
    }

    @Override
    public Completable updateMonsterHorror(Integer actualHorror) {
        return testView.updateMonsterHorror(actualHorror);
    }

    @Override
    public Completable updateMonsterDamage(Integer actualDamage) {
        return testView.updateMonsterDamage(actualDamage);
    }
}
