package sk.sivak.eldritchhorror.core.eventlistener.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

/** Called only by audited prompts whose affirmative handler has no cost or exhaustion. */
public final class FreeRerollPolicy {
    private FreeRerollPolicy() { }

    public static boolean canAutoAccept(TestData test, int count) {
        ServicePlatform platform = ServicePlatform.get();
        if (platform.getAssetDeck() == null || platform.getInvestigators() == null || platform.getTestFlavor() == null)
            return false;
        return canAutoAccept(test, count,
                platform.getAssetDeck().getAssets(platform.getInvestigators().getActiveInvestigatorId()),
                platform.getTestFlavor().getFlavorType());
    }

    public static boolean canAutoAccept(TestData test, int count, List<AssetInfo> assets, TestFlavorType flavor) {
        if (!test.areLowestDiceFailures(count) || test.hasReachedMinScoreToEndTest() || assets == null) return false;
        List<DiceRoll> ordered = new ArrayList<>(test.getDiceRolls());
        Collections.sort(ordered, (a, b) -> Integer.compare(a.getDiceValue(), b.getDiceValue()));
        boolean combatStrength = flavor == TestFlavorType.COMBAT && test.getStat() == Stat.STRENGTH;
        boolean blunderbuss = false;
        if (combatStrength) {
            for (UsableAsset selected : test.getSelectedUsableAssets()) {
                if (selected.getCardInfo() instanceof AssetInfo
                        && ((AssetInfo) selected.getCardInfo()).getId() == AssetId.BLUNDERBUSS) blunderbuss = true;
            }
        }
        int availableIncrements = 0;
        for (AssetInfo asset : assets) {
            if (asset.isDisabled()) continue;
            switch (asset.getId()) {
                case LUCKY_CIGARETTE_CASE: availableIncrements++; break;
                case DOT_18_DERRINGER: if (combatStrength) availableIncrements++; break;
                case GRUESOME_TALISMAN:
                    if (flavor == TestFlavorType.COMBAT && test.getStat() == Stat.WILL) availableIncrements++;
                    break;
                case EXPEDITION_MAP:
                    if (flavor == TestFlavorType.EXPEDITION || flavor == TestFlavorType.MYSTIC_RUINS) availableIncrements++;
                    break;
                default: break;
            }
        }
        for (int i = 0; i < count; i++) {
            int value = ordered.get(i).getDiceValue();
            // Rerolling an existing 1 cannot add another Blunderbuss penalty; rerolling 2-4 can.
            if (blunderbuss && value != 1) return false;
            // Preserve the player's chance to convert this die with later +1 abilities, including stacked ones.
            if (availableIncrements > 0 && value + availableIncrements >= test.getMinSuccessDiceValue()) return false;
        }
        return true;
    }
}