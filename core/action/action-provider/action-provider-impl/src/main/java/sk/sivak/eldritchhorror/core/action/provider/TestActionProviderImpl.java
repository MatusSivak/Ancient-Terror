package sk.sivak.eldritchhorror.core.action.provider;

import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.action.HookableAction;
import sk.sivak.eldritchhorror.core.action.impl.test.AddOneToDieResultAction;
import sk.sivak.eldritchhorror.core.action.impl.test.AddOneToDieResultEventAction;
import sk.sivak.eldritchhorror.core.action.impl.test.CalculateDicePoolAction;
import sk.sivak.eldritchhorror.core.action.impl.test.ConfirmTestAction;
import sk.sivak.eldritchhorror.core.action.impl.test.ConfirmTestResultAction;
import sk.sivak.eldritchhorror.core.action.impl.test.EvaluateRolledDicesAction;
import sk.sivak.eldritchhorror.core.action.impl.test.FindCountRerollDiceAction;
import sk.sivak.eldritchhorror.core.action.impl.test.FindMinSuccessDiceValueAction;
import sk.sivak.eldritchhorror.core.action.impl.test.ReadStatAction;
import sk.sivak.eldritchhorror.core.action.impl.test.RegisterBonusDiceAction;
import sk.sivak.eldritchhorror.core.action.impl.test.RegisterUsableAssetsAction;
import sk.sivak.eldritchhorror.core.action.impl.test.RerollDiceAction;
import sk.sivak.eldritchhorror.core.action.impl.test.RerollUsingAssetsAction1;
import sk.sivak.eldritchhorror.core.action.impl.test.RerollUsingAssetsAction2;
import sk.sivak.eldritchhorror.core.action.impl.test.RerollUsingAssetsAction3;
import sk.sivak.eldritchhorror.core.action.impl.test.RerollUsingClueAction;
import sk.sivak.eldritchhorror.core.action.impl.test.RerollUsingFocusAction;
import sk.sivak.eldritchhorror.core.action.impl.test.RollAndShowDieAction;
import sk.sivak.eldritchhorror.core.action.impl.test.RollDicesAction;
import sk.sivak.eldritchhorror.core.action.impl.test.SetTestFlavorAction;
import sk.sivak.eldritchhorror.core.action.impl.test.ShowRolledDicesAction;
import sk.sivak.eldritchhorror.core.action.impl.test.TestStartAction;
import sk.sivak.eldritchhorror.core.action.impl.test.UnsetTestFlavorAction;
import sk.sivak.eldritchhorror.core.action.impl.test.UpdateScoreUsingAssets;
import sk.sivak.eldritchhorror.core.action.impl.test.UseAssetsInTestAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.AfterDamageCheckAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.AfterHorrorCheckAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.BeforeDamageTestAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.BeforeHorrorTestAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.CheckIfInvestigatorWasNotDefeatedInCombatAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.DestroyDamageAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.DestroyHealthAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.DestroyHorrorAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.DestroyMonsterHealthAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.DestroySanityAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.EndOfCombatEventAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.HideCombatTableAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.HighlightDamageCombatAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.HighlightHorrorCombatAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.JustAfterDamageTestAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.SanityLostInHorrorCheckAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.ShowCombatBackgroundAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.ShowCombatOverviewAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.ShowCombatTableAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.UpdateMonsterDamageAction;
import sk.sivak.eldritchhorror.core.action.impl.test.combat.UpdateMonsterHorrorAction;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.data.PrimitiveWrapper;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.AddOneToDieResultData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.CountRerollDiceData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RerollUsingData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import java.util.List;

/**
 * @author msivak
 */
public class TestActionProviderImpl implements TestActionProvider {

    @Override
    public Action<Object, TestData> testStart(TestData testData) {
        return new TestStartAction(testData);
    }

    @Override
    public HookableAction<TestData, TestData> readStatAction() {
        return new ReadStatAction();
    }

    @Override
    public HookableAction<TestData, TestData> confirmTest() {
        return new ConfirmTestAction();
    }

    @Override
    public DirectEventAction<TestData> registerUsableAssets() {
        return new RegisterUsableAssetsAction();
    }

    @Override
    public DirectEventAction<TestData> registerBonusDice() {
        return new RegisterBonusDiceAction();
    }

    @Override
    public Action<TestData, TestData> useAssetsInTest() {
        return new UseAssetsInTestAction();
    }

    @Override
    public HookableAction<TestData, TestData> calculateDicePool() {
        return new CalculateDicePoolAction();
    }

    @Override
    public HookableAction<TestData, TestData> rollDices() {
        return new RollDicesAction();
    }

    @Override
    public HookableAction<PrimitiveWrapper<Integer>, PrimitiveWrapper<Integer>> findMinSuccessDiceValue() {
        return new FindMinSuccessDiceValueAction();
    }

    @Override
    public HookableAction<TestData, TestData> evaluateRolledDices() {
        return new EvaluateRolledDicesAction();
    }

    @Override
    public HookableAction<TestData, TestData> showRolledDices() {
        return new ShowRolledDicesAction();
    }

    @Override
    public HookableAction<TestData, TestData> confirmTestResult() {
        return new ConfirmTestResultAction();
    }

    @Override
    public DirectEventAction<TestData> rerollUsingAssets1() {
        return new RerollUsingAssetsAction1();
    }

    @Override
    public DirectEventAction<TestData> rerollUsingAssets2() {
        return new RerollUsingAssetsAction2();
    }

    @Override
    public DirectEventAction<TestData> rerollUsingAssets3() {
        return new RerollUsingAssetsAction3();
    }

    @Override
    public HookableAction<CountRerollDiceData, TestData> rerollDice(List<DiceRoll> dicesToReroll) {
        return new RerollDiceAction(dicesToReroll);
    }

    @Override
    public HookableAction<RerollUsingData, RerollData> rerollUsingFocus() {
        return new RerollUsingFocusAction();
    }

    @Override
    public HookableAction<RerollUsingData, RerollData> rerollUsingClue() {
        return new RerollUsingClueAction();
    }

    @Override
    public HookableAction<RollData, Integer> rollAndShowDie() {
        return new RollAndShowDieAction();
    }

    @Override
    public HookableAction<CountRerollDiceData, CountRerollDiceData> findCountRerollDice() {
        return new FindCountRerollDiceAction();
    }

    @Override
    public DirectEventAction<AddOneToDieResultData> addOneToDieResultEvent() {
        return new AddOneToDieResultEventAction();
    }

    @Override
    public HookableAction<AddOneToDieResultData, AddOneToDieResultData> addOneToDieResult() {
        return new AddOneToDieResultAction();
    }

    @Override
    public DirectEventAction<TestData> updateScoreUsingAssets() {
        return new UpdateScoreUsingAssets();
    }

    @Override
    public Action<Object, Object> setTestFlavor(TestFlavorRequest testFlavorRequest) {
        return new SetTestFlavorAction(testFlavorRequest);
    }

    @Override
    public Action<Object, Object> unsetTestFlavor() {
        return new UnsetTestFlavorAction();
    }

    // COMBAT


    @Override
    public HookableAction<CombatData, CombatData> showCombatOverview() {
        return new ShowCombatOverviewAction();
    }

    @Override
    public Action<Object, Void> highlightHorrorCombat() {
        return new HighlightHorrorCombatAction();
    }

    @Override
    public Action<Object, Void> highlightDamageCombat() {
        return new HighlightDamageCombatAction();
    }

    @Override
    public Action<Object, Void> destroyHorror(List<DiceRoll> diceRolls) {
        return new DestroyHorrorAction(diceRolls);
    }

    @Override
    public Action<Object, Void> destroyDamage(List<DiceRoll> diceRolls) {
        return new DestroyDamageAction(diceRolls);
    }

    @Override
    public HookableAction<CombatData, CombatData> destroyMonsterHealth(List<DiceRoll> diceRolls) {
        return new DestroyMonsterHealthAction(diceRolls);
    }

    @Override
    public Action<Object, Void> destroySanity(int sanityLost) {
        return new DestroySanityAction(sanityLost);
    }

    @Override
    public HookableAction<CombatData, CombatData> destroyHealth() {
        return new DestroyHealthAction();
    }

    @Override
    public HookableAction<CombatData, CombatData> showCombatTable() {
        return new ShowCombatTableAction();
    }

    @Override
    public HookableAction<Object, Void> hideCombatTable() {
        return new HideCombatTableAction();
    }

    @Override
    public DirectEventAction<CombatData> beforeHorrorTest() {
        return new BeforeHorrorTestAction();
    }

    @Override
    public DirectEventAction<CombatData> beforeDamageTest() {
        return new BeforeDamageTestAction();
    }

    @Override
    public HookableAction<CombatData, CombatData> afterDamageCheck() {
        return new AfterDamageCheckAction();
    }

    @Override
    public HookableAction<CombatData, CombatData> afterHorrorCheck() {
        return new AfterHorrorCheckAction();
    }

    @Override
    public HookableAction<CombatData, CombatData> updateMonsterHorror() {
        return new UpdateMonsterHorrorAction();
    }

    @Override
    public HookableAction<CombatData, CombatData> updateMonsterDamage() {
        return new UpdateMonsterDamageAction();
    }

    @Override
    public Action<Object, Void> showCombatBackground() {
        return new ShowCombatBackgroundAction();
    }

    @Override
    public HookableAction<Object, CombatData> endOfCombatEvent(CombatData combatData) {
        return new EndOfCombatEventAction(combatData);
    }

    @Override
    public DirectEventAction<CombatData> sanityLostInHorrorCheck() {
        return new SanityLostInHorrorCheckAction();
    }

    @Override
    public Action<Object, Object> checkIfInvestigatorWasNotDefeatedInCombat() {
        return new CheckIfInvestigatorWasNotDefeatedInCombatAction();
    }

    @Override
    public DirectEventAction<CombatData> justAfterDamageTest() {
        return new JustAfterDamageTestAction();
    }
}
