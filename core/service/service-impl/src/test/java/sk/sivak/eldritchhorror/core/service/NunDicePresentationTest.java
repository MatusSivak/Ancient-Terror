package sk.sivak.eldritchhorror.core.service;

import java.util.*;
import org.junit.*;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.action.impl.test.*;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.blessed.BlessedCondition;
import sk.sivak.eldritchhorror.core.constants.condition.cursed.CursedCondition;
import sk.sivak.eldritchhorror.core.constants.condition.debt.DebtCondition;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.test.*;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.eventtype.data.test.*;
import sk.sivak.eldritchhorror.core.model.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NunDicePresentationTest {
    private TestController controller;
    private ModelWrite model;
    private final PublishSubject<Void> raw = PublishSubject.create();
    private final PublishSubject<Void> bonus = PublishSubject.create();
    private List<DiceRoll> shownBonus;

    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    private void setup(ConditionInfo condition) {
        InvestigatorWrite nun = mock(InvestigatorWrite.class, RETURNS_DEEP_STUBS);
        when(nun.getInfo().getInvestigatorId()).thenReturn(InvestigatorId.THE_NUN);
        InvestigatorsWrite investigators = mock(InvestigatorsWrite.class);
        when(investigators.getActiveInvestigator()).thenReturn(nun);
        ServicePlatform.get().setInvestigators(investigators);
        TestFlavorWrite flavor = mock(TestFlavorWrite.class);
        when(flavor.getFlavorType()).thenReturn(TestFlavorType.CONDITION);
        when(flavor.getFlavorData()).thenReturn(condition);
        ServicePlatform.get().setTestFlavor(flavor);
        model = mock(ModelWrite.class); ServicePlatform.get().setModel(model);
        controller = mock(TestController.class); ServicePlatform.get().setTestController(controller);
        when(controller.showRolledTestDices(anyList())).thenReturn(raw.toCompletable());
        when(controller.rerollDice(anyList())).thenReturn(raw.toCompletable());
        when(controller.showNunDiceBonus(anyList())).thenAnswer(c -> {
            shownBonus = c.getArgument(0); return bonus.toCompletable();
        });
    }

    @Test public void multiDieBoonTestWaitsForRawRollThenBonusBeforeChangingScore() {
        setup(new BlessedCondition());
        when(model.rollDie()).thenReturn(1, 4, 6);
        TestData test = new TestData(); test.setCalculatedDicePool(3); test.setMinSuccessDiceValue(5);
        RollDicesAction roll = new RollDicesAction(); roll.setInput(test); roll.execute().toBlocking().value();
        assertValues(test.getDiceRolls(), 1, 4, 6);
        EvaluateRolledDicesAction evaluate = new EvaluateRolledDicesAction();
        evaluate.setInput(test); evaluate.execute().toBlocking().value();
        ShowRolledDicesAction show = new ShowRolledDicesAction(); show.setInput(test);
        int[] done = {0}; show.execute().subscribe(result -> done[0]++);
        verify(controller, never()).showNunDiceBonus(anyList());
        raw.onCompleted();
        assertValues(shownBonus, 2, 5, 6);
        assertValues(test.getDiceRolls(), 1, 4, 6);
        assertEquals(0, done[0]);
        bonus.onCompleted();
        assertValues(test.getDiceRolls(), 2, 5, 6);
        assertEquals(2, test.getScore()); assertEquals(1, done[0]);
    }

    @Test public void baneRerollAddsBonusOnlyToNewlyRolledDice() {
        setup(new CursedCondition());
        when(model.rollDie()).thenReturn(5);
        DiceRoll unchanged = die(0, 4), rerolled = die(1, 2);
        TestData test = new TestData(); test.setMinSuccessDiceValue(6);
        test.setDiceRolls(Arrays.asList(unchanged, rerolled));
        CountRerollDiceData input = new CountRerollDiceData(); input.setTestData(test); input.setCount(1);
        RerollDiceAction reroll = new RerollDiceAction(Collections.singletonList(rerolled)); reroll.setInput(input);
        int[] done = {0}; reroll.execute().subscribe(result -> done[0]++);
        assertValues(test.getDiceRolls(), 4, 5);
        raw.onCompleted(); assertValues(shownBonus, 6); assertEquals(1, shownBonus.get(0).getDiceNr());
        assertEquals(0, done[0]);
        bonus.onCompleted(); assertValues(test.getDiceRolls(), 4, 6);
        assertEquals(DiceRoll.Score.GOOD, rerolled.getScore()); assertEquals(1, done[0]);
    }

    @Test public void unrelatedConditionDoesNotShowBonus() {
        setup(new DebtCondition());
        List<DiceRoll> dice = Collections.singletonList(die(0, 2));
        InvestigatorDiceModifiers.applyAfterRoll(dice, null, 5).await();
        verify(controller, never()).showNunDiceBonus(anyList());
        assertValues(dice, 2);
    }

    private DiceRoll die(int number, int value) {
        DiceRoll die = new DiceRoll(); die.setDiceNr(number); die.setDiceValue(value);
        die.setScore(DiceRoll.Score.BAD); return die;
    }

    private void assertValues(List<DiceRoll> dice, int... values) {
        assertEquals(values.length, dice.size());
        for (int i = 0; i < values.length; i++) assertEquals(values[i], dice.get(i).getDiceValue());
    }
}
