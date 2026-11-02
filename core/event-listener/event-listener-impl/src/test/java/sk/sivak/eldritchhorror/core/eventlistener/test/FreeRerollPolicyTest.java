package sk.sivak.eldritchhorror.core.eventlistener.test;

import java.util.*;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.*;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import static org.junit.Assert.*;

public class FreeRerollPolicyTest {
    @Test public void blunderbussCanRerollOnesButMustAskBeforeRiskingAnotherOne() {
        TestData data = data(Stat.STRENGTH, 3, 5);
        UsableAsset selected = new UsableAsset();
        selected.setCardInfo(new BlunderbussAsset());
        data.setSelectedUsableAssets(Collections.singletonList(selected));
        assertFalse(allowed(data, TestFlavorType.COMBAT));
        data.getDiceRolls().get(0).setDiceValue(1);
        assertTrue(allowed(data, TestFlavorType.COMBAT));
        data.getDiceRolls().get(0).setDiceValue(3);
        assertTrue(allowed(data, TestFlavorType.OTHER_WORLD));
        data.setSelectedUsableAssets(Collections.emptyList());
        assertTrue(allowed(data, TestFlavorType.COMBAT, new BlunderbussAsset()));
    }

    @Test public void eachApplicableDieIncrementKeepsANearSuccessManual() {
        assertFalse(allowed(data(Stat.STRENGTH, 4), TestFlavorType.COMBAT, new Dot18DerringerAsset()));
        assertFalse(allowed(data(Stat.WILL, 4), TestFlavorType.COMBAT, new GruesomeTalismanAsset()));
        assertFalse(allowed(data(Stat.LORE, 4), TestFlavorType.EXPEDITION, new ExpeditionMapAsset()));
        assertFalse(allowed(data(Stat.LORE, 4), TestFlavorType.MYSTIC_RUINS, new ExpeditionMapAsset()));
        assertFalse(allowed(data(Stat.INFLUENCE, 4), TestFlavorType.OTHER_WORLD, new LuckyCigaretteCaseAsset()));
    }

    @Test public void disabledAndIrrelevantBonusesDoNotBlockFreeRerolls() {
        LuckyCigaretteCaseAsset disabled = new LuckyCigaretteCaseAsset() {
            @Override public boolean isDisabled() { return true; }
        };
        assertTrue(allowed(data(Stat.INFLUENCE, 4), TestFlavorType.COMBAT, disabled));
        assertTrue(allowed(data(Stat.INFLUENCE, 4), TestFlavorType.COMBAT,
                new Dot18DerringerAsset(), new GruesomeTalismanAsset(), new ExpeditionMapAsset()));
        assertTrue(allowed(data(Stat.STRENGTH, 2, 4), TestFlavorType.COMBAT, new Dot18DerringerAsset()));
    }

    @Test public void stackedBonusesAndChangedSuccessThresholdsAreRespected() {
        assertFalse(allowed(data(Stat.STRENGTH, 3), TestFlavorType.COMBAT,
                new Dot18DerringerAsset(), new LuckyCigaretteCaseAsset()));
        TestData blessed = data(Stat.STRENGTH, 3);
        blessed.setMinSuccessDiceValue(4);
        assertFalse(allowed(blessed, TestFlavorType.COMBAT, new Dot18DerringerAsset()));
        TestData cursed = data(Stat.STRENGTH, 4);
        cursed.setMinSuccessDiceValue(6);
        assertTrue(allowed(cursed, TestFlavorType.COMBAT, new Dot18DerringerAsset()));
    }

    private boolean allowed(TestData data, TestFlavorType flavor, AssetInfo... assets) {
        return FreeRerollPolicy.canAutoAccept(data, 1, Arrays.asList(assets), flavor);
    }

    private TestData data(Stat stat, int... values) {
        TestData result = new TestData();
        result.setStat(stat);
        result.setMinSuccessDiceValue(5);
        result.setMinScoreToEndTest(10);
        List<DiceRoll> dice = new ArrayList<>();
        for (int value : values) {
            DiceRoll die = new DiceRoll(value >= 5 ? DiceRoll.Score.GOOD : DiceRoll.Score.BAD);
            die.setDiceValue(value);
            dice.add(die);
        }
        result.setDiceRolls(dice);
        return result;
    }
}