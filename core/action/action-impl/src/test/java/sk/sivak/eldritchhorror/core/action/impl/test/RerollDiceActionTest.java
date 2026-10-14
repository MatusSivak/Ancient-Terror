package sk.sivak.eldritchhorror.core.action.impl.test;

import java.lang.reflect.Proxy;
import java.util.*;
import org.junit.After;
import org.junit.Test;
import rx.Completable;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.controller.TestController;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.data.test.*;
import static org.junit.Assert.*;

public class RerollDiceActionTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void twoDieRerollWithOnlyOneDieStillCompletes() {
        DiceRoll die = die(2, 0);
        TestData test = run(Arrays.asList(die), 2, null, 6);
        assertEquals(6, die.getDiceValue());
        assertEquals(DiceRoll.Score.GOOD, die.getScore());
        assertEquals(1, test.getDiceRolls().size());
    }

    @Test public void emptyPoolCompletesWithoutStartingAnAnimation() {
        run(Collections.emptyList(), 2, null);
    }

    @Test public void automaticRerollOnlyChangesLowestDiceAndPreservesOriginalOrder() {
        DiceRoll good = die(6, 0), low = die(1, 1), middle = die(3, 2);
        TestData test = run(Arrays.asList(good, low, middle), 2, null, 5, 2);
        assertEquals(Arrays.asList(good, low, middle), test.getDiceRolls());
        assertEquals(6, good.getDiceValue());
        assertEquals(5, low.getDiceValue());
        assertEquals(2, middle.getDiceValue());
        assertEquals(DiceRoll.Score.BAD, middle.getScore());
    }

    @Test public void explicitSelectionIsPreserved() {
        DiceRoll low = die(1, 0), selected = die(3, 1);
        run(Arrays.asList(low, selected), 2, Collections.singletonList(selected), 6);
        assertEquals(1, low.getDiceValue());
        assertEquals(6, selected.getDiceValue());
    }

    private TestData run(List<DiceRoll> dice, int count, List<DiceRoll> selected, Integer... values) {
        TestData test = new TestData();
        test.setDiceRolls(new ArrayList<>(dice));
        test.setMinSuccessDiceValue(5);
        CountRerollDiceData input = new CountRerollDiceData();
        input.setTestData(test); input.setCount(count);
        for (int value : values) input.addRerolledValue(value);
        int[] animations = {0};
        ServicePlatform.get().setTestController((TestController) Proxy.newProxyInstance(
                TestController.class.getClassLoader(), new Class[]{TestController.class}, (p,m,a) -> {
                    assertEquals("rerollDice", m.getName());
                    List<DiceRoll> rerolled = (List<DiceRoll>) a[0];
                    assertFalse(rerolled.isEmpty());
                    assertEquals(selected == null ? Math.min(count, dice.size()) : selected.size(), rerolled.size());
                    animations[0]++;
                    return Completable.complete();
                }));
        RerollDiceAction action = new RerollDiceAction(selected); action.setInput(input);
        assertSame(test, action.execute().toBlocking().value());
        assertEquals(dice.isEmpty() ? 0 : 1, animations[0]);
        return test;
    }

    private DiceRoll die(int value, int index) {
        DiceRoll die = new DiceRoll(value >= 5 ? DiceRoll.Score.GOOD : DiceRoll.Score.BAD);
        die.setDiceValue(value); die.setDiceNr(index); return die;
    }
}
