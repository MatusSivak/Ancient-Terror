package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.List;

/**
 * @author msivak
 */
public interface TestService extends CommonService {
    Single<TestData> test(Stat stat, int modifier, int minScoreToEndTest);

    Single<TestData> test(Stat stat, int modifier, int minScoreToEndTest, TestFlavorRequest testFlavorRequest);

    Single<Integer> rollDie(RollData rollData);

    void findCountRerollDice(CountRerollDiceData.CountRerollDiceType type, TestData testData);

    void unsetTestFlavor();

    void setTestFlavor(TestFlavorRequest testFlavorRequest);

    void addOneToDieResult();

    void rerollUsingFocus();

    void rerollUsingClue();

    void findCountRerollDice(int defaultCount, CountRerollDiceData.CountRerollDiceType type, TestData testData);

    void rerollDie(TestData testData);

    void rerollDice();


    void rerollDice(List<DiceRoll> dicesToReroll);

    void combat(MonsterInfo monsterInfo);

    void destroyMonsterHealth(List<DiceRoll> diceRolls);

    void updateMonsterDamage(CombatData combatData);

    void updateMonsterHorror(CombatData combatData);
}
