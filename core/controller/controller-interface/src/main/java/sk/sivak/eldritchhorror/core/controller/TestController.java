package sk.sivak.eldritchhorror.core.controller;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;

import java.util.List;

/**
 * @author msivak
 */
public interface TestController {
    Single<List<UsableAsset>> confirmTest(Stat stat, int modifier, int baseStatValue, int bonusStatValue,
                                          List<UsableAsset> usableAssets, int additionalDicesCount, boolean isCombat);

    Completable showRolledTestDices(List<DiceRoll> input);

    Completable showRolledDices(List<DiceRoll> input);

    Completable confirmTestResult(boolean scoreImportant, boolean successful, int score);

    Completable confirmRollResult(int score);

    Completable rerollDice(List<DiceRoll> diceRoll);

    Single<Boolean> askRerollUsingFocus(Question question);

    Single<Boolean> askRerollUsingClue(Question question);

    Single<Integer> addOneToDieResult(int minSuccess);

    Completable showCombatOverview(InvestigatorId investigatorId, MonsterInfo monsterInfo, Stat damageTestType);

    Completable highlightHorrorCombat();

    Completable highlightDamageCombat();

    Completable destroyHorror(List<DiceRoll> diceRolls);

    Completable destroyDamage(List<DiceRoll> diceRolls);

    Completable destroyMonsterHealth(List<DiceRoll> diceRolls);

    Completable destroySanity(int sanityLost);

    Completable destroyHealth(int healthLost);

    Completable showCombatTable(MonsterInfo monsterInfo, Integer actualHorror, Integer actualDamage);

    Completable hideCombatTable();

    Completable waitForCombatTableCentered();

    Completable updateMonsterHorror(Integer actualHorror);

    Completable updateMonsterDamage(Integer actualDamage);


}
