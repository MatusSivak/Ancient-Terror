package sk.sivak.eldritchhorror.core.view;

import rx.Completable;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.combat.CombatOverviewTableData;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;

import java.util.List;

/**
 * @author msivak
 */
public interface TestView {

    Single<List<UsableAsset>> confirmTest(Stat stat, int modifier, int baseStatValue, int bonusStatValue,
                                          List<UsableAsset> usableAssets, int additionalDicesCount, boolean isCombat);

    Completable showRolledTestDices(List<DiceRoll> diceRolls);

    Completable showRolledDices(List<DiceRoll> diceRolls);

    Completable confirmTestResult(boolean scoreImportant, boolean successful, int score);

    Completable confirmRollResult(int score);

    Completable rerollDice(List<DiceRoll> diceRoll);

    Single<Boolean> askRerollUsingFocus(Question question);

    Single<Boolean> askRerollUsingClue(Question question);

    Single<Integer> addOneToDieResult(int minSuccess);

    Completable showCombatOverview(CombatOverviewTableData data);

    Completable highlightHorrorCombat();

    Completable highlightDamageCombat();

    Completable destroyHorror(List<DiceRoll> diceRolls);

    Completable destroyDamage(List<DiceRoll> diceRolls);

    Completable destroySanity(int sanityLost);

    Completable destroyHealth(int healthLost);

    Completable destroyMonsterHealth(List<DiceRoll> diceRolls);

    Completable showCombatTable(MonsterCombatTableData data);

    Completable hideCombatTable();

    Completable waitForCombatTableCentered();

    Completable updateMonsterHorror(Integer actualHorror);

    Completable updateMonsterDamage(Integer actualDamage);
}
