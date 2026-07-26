package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.data.PrimitiveWrapper;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.*;

import java.util.List;

/**
 * @author msivak
 */
public interface TestActionProvider {

    Action<Object, TestData> testStart(TestData testData);

    HookableAction<TestData, TestData> readStatAction();

    HookableAction<TestData, TestData> confirmTest();

    DirectEventAction<TestData> registerUsableAssets();

    DirectEventAction<TestData> registerBonusDice();

    Action<TestData, TestData> useAssetsInTest();

    HookableAction<TestData, TestData> calculateDicePool();

    HookableAction<TestData, TestData> rollDices();

    HookableAction<PrimitiveWrapper<Integer>, PrimitiveWrapper<Integer>> findMinSuccessDiceValue();

    HookableAction<TestData, TestData> evaluateRolledDices();

    HookableAction<TestData, TestData> showRolledDices();

    HookableAction<TestData, TestData> confirmTestResult();

    DirectEventAction<TestData> rerollUsingAssets1();

    DirectEventAction<TestData> rerollUsingAssets2();

    DirectEventAction<TestData> rerollUsingAssets3();

    HookableAction<CountRerollDiceData, TestData> rerollDice(List<DiceRoll> dicesToReroll);

    HookableAction<RerollUsingData, RerollData> rerollUsingFocus();

    HookableAction<RerollUsingData, RerollData> rerollUsingClue();

    HookableAction<RollData, Integer> rollAndShowDie();

    HookableAction<CountRerollDiceData, CountRerollDiceData> findCountRerollDice();

    DirectEventAction<AddOneToDieResultData> addOneToDieResultEvent();

    HookableAction<AddOneToDieResultData, AddOneToDieResultData> addOneToDieResult();

    DirectEventAction<TestData> updateScoreUsingAssets();

    Action<Object, Object> setTestFlavor(TestFlavorRequest testFlavorRequest);

    Action<Object, Object> unsetTestFlavor();

    // COMBAT
    HookableAction<CombatData, CombatData> showCombatOverview();

    Action<Object, Void> highlightHorrorCombat();

    Action<Object, Void> highlightDamageCombat();

    Action<Object, Void> destroyHorror(List<DiceRoll> diceRolls);

    Action<Object, Void> destroyDamage(List<DiceRoll> diceRolls);

    HookableAction<CombatData, CombatData> destroyMonsterHealth(List<DiceRoll> diceRolls);

    Action<Object, Void> destroySanity(int sanityLost);

    HookableAction<CombatData, CombatData> destroyHealth();

    HookableAction<CombatData, CombatData> showCombatTable();

    HookableAction<Object, Void> hideCombatTable();

    DirectEventAction<CombatData> beforeDamageTest();

    DirectEventAction<CombatData> beforeHorrorTest();

    HookableAction<CombatData, CombatData> afterDamageCheck();

    HookableAction<CombatData, CombatData> afterHorrorCheck();

    HookableAction<CombatData, CombatData> updateMonsterHorror();

    HookableAction<CombatData, CombatData> updateMonsterDamage();

    Action<Object, Void> showCombatBackground();

    HookableAction<Object, CombatData> endOfCombatEvent(CombatData combatData);

    DirectEventAction<CombatData> sanityLostInHorrorCheck();

    Action<Object, Object> checkIfInvestigatorWasNotDefeatedInCombat();

    DirectEventAction<CombatData> justAfterDamageTest();
}
