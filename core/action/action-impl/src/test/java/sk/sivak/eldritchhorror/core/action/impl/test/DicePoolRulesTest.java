package sk.sivak.eldritchhorror.core.action.impl.test;

import org.junit.Test;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;

/** FFG FAQ (2017-08-11), Core Mechanics: minimum is applied to the complete pool. */
public class DicePoolRulesTest {
    @Test public void strengthOneMinusTwoIncludesAdditionalDiceBeforeTheMinimum() {
        TestData test = new TestData(); test.setBaseStatValue(1); test.setModifier(-2);
        for (int extra = 0; extra <= 4; extra++) {
            test.setAdditionalDicesCount(extra);
            assertEquals("Additional dice: " + extra, Math.max(1, extra - 1), test.calculateDicePool());
        }
    }

    @Test public void highestSkillBonusButAllAdditionalDiceContribute() {
        TestData test = new TestData(); test.setBaseStatValue(1); test.setModifier(-2);
        UsableAsset first = new UsableAsset(); first.setStatBonus(2); first.setDicePoolBonus(1);
        UsableAsset second = new UsableAsset(); second.setStatBonus(3); second.setDicePoolBonus(1);
        test.setSelectedUsableAssets(Arrays.asList(first, second));
        test.setBonusStatValue(1); test.setAdditionalDicesCount(1);
        assertEquals(6, test.calculateDicePool());
    }

    @Test public void cardMinimumIsAnExplicitExceptionAndIsNotAnAdditionalDie() {
        TestData test = new TestData(); test.setBaseStatValue(1); test.setModifier(-2);
        test.setMinimumRolledDice(2); test.setAdditionalDicesCount(1);
        assertEquals(2, test.calculateDicePool());
        test.setBaseStatValue(5);
        assertEquals(4, test.calculateDicePool());
    }
}
