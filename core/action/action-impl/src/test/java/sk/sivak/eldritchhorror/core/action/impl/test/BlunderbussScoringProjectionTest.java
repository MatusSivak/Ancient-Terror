package sk.sivak.eldritchhorror.core.action.impl.test;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Blunderbuss: each 6 = 2 successes, each 1 cancels one; rerolls must see that outcome. */
public class BlunderbussScoringProjectionTest {

    private static TestData rolled(int... values) {
        List<DiceRoll> rolls = new ArrayList<>();
        for (int value : values) {
            DiceRoll roll = new DiceRoll(value >= 5 ? DiceRoll.Score.GOOD : DiceRoll.Score.BAD);
            roll.setDiceValue(value);
            rolls.add(roll);
        }
        TestData test = new TestData();
        test.setDiceRolls(rolls);
        test.setMinScoreToEndTest(1);
        return test;
    }

    @Test public void aOneThatWillCancelTheOnlySuccessKeepsTheTestOpenForRerolls() {
        TestData test = rolled(5, 1);
        assertTrue(test.hasReachedMinScoreToEndTest());
        test.setPendingSixesDoubleOnesCancel(true);
        assertFalse(test.hasReachedMinScoreToEndTest());
    }

    @Test public void aSixCountsDoubleAndAbsorbsAOne() {
        TestData test = rolled(6, 1);
        test.setPendingSixesDoubleOnesCancel(true);
        assertTrue(test.hasReachedMinScoreToEndTest());
        test.setMinScoreToEndTest(2);
        assertFalse(test.hasReachedMinScoreToEndTest());
    }

    @Test public void aSixAloneReachesTwoSuccessesWithoutAReroll() {
        TestData test = rolled(6, 3);
        test.setMinScoreToEndTest(2);
        assertFalse(test.hasReachedMinScoreToEndTest());
        test.setPendingSixesDoubleOnesCancel(true);
        assertTrue(test.hasReachedMinScoreToEndTest());
    }
}
