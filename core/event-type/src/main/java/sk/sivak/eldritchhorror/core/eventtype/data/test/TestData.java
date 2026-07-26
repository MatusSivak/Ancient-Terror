package sk.sivak.eldritchhorror.core.eventtype.data.test;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java8.features.stream.Stream.anyMatch;
import static java8.features.stream.Stream.count;

/**
 * @author msivak
 */
public class TestData {

    public static final int JUST_ONE = 1;
    private boolean isCombat;
    private Stat stat;
    private int modifier;
    private int minScoreToEndTest;

    private int baseStatValue;
    private int bonusStatValue;
    private int minimumRolledDice = 1;

    private List<UsableAsset> usableAssets = new LinkedList<>();
    private List<UsableAsset> selectedUsableAssets = new LinkedList<>();
    private List<DiceRoll> diceRolls;
    private int additionalDicesCount = 0;
    private int scoreBonus = 0;
    private int minScoreToBeSuccessful = 1;
    private int calculatedDicePool;

    private int minSuccessDiceValue;

    public int getMinSuccessDiceValue() {
        return minSuccessDiceValue;
    }

    public void setMinSuccessDiceValue(int minSuccessDiceValue) {
        this.minSuccessDiceValue = minSuccessDiceValue;
    }

    public List<DiceRoll> getDiceRolls() {
        return diceRolls;
    }

    public int getMinimumRolledDice() {
        return minimumRolledDice;
    }

    public void setMinimumRolledDice(int minimumRolledDice) {
        this.minimumRolledDice = minimumRolledDice;
    }

    public void setDiceRolls(List<DiceRoll> diceRolls) {
        this.diceRolls = diceRolls;
    }

    public int getCalculatedDicePool() {
        return calculatedDicePool;
    }

    public void setCalculatedDicePool(int calculatedDicePool) {
        this.calculatedDicePool = calculatedDicePool;
    }

    public List<UsableAsset> getSelectedUsableAssets() {
        return selectedUsableAssets;
    }

    public void setSelectedUsableAssets(List<UsableAsset> selectedUsableAssets) {
        this.selectedUsableAssets = selectedUsableAssets;
    }

    public void addUsableAsset(UsableAsset usableAsset) {
        usableAssets.add(usableAsset);
    }

    public List<UsableAsset> getUsableAssets() {
        return new ArrayList<>(usableAssets);
    }

    public void removeDisabledUsableAssets() {
        IterableUtils.removeIf(usableAssets, usableAsset -> usableAsset.getCardInfo().isDisabled());
    }

    public void removeAllNonMagicalUsableAssets() {
        IterableUtils.removeIf(usableAssets, usableAsset ->
                usableAsset.getCardInfo() instanceof AssetInfo &&
                !((AssetInfo) usableAsset.getCardInfo()).getTraits().contains(AssetTrait.MAGICAL));
        IterableUtils.removeIf(usableAssets, usableAsset ->
                usableAsset.getCardInfo() instanceof ArtifactInfo &&
                        !((ArtifactInfo) usableAsset.getCardInfo()).getTraits().contains(AssetTrait.MAGICAL));
    }

    public Stat getStat() {
        return stat;
    }

    public void setStat(Stat stat) {
        this.stat = stat;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }


    public int getMinScoreToEndTest() {
        return minScoreToEndTest;
    }

    public void setMinScoreToEndTest(int minScoreToEndTest) {
        this.minScoreToEndTest = minScoreToEndTest;
    }

    public int getBaseStatValue() {
        return baseStatValue;
    }

    public void setBaseStatValue(int baseStatValue) {
        this.baseStatValue = baseStatValue;
    }

    public int getBonusStatValue() {
        return bonusStatValue;
    }

    public void setBonusStatValue(int bonusStatValue) {
        this.bonusStatValue = bonusStatValue;
    }

    public int getAdditionalDicesCount() {
        return additionalDicesCount;
    }

    public void setAdditionalDicesCount(int additionalDicesCount) {
        this.additionalDicesCount = additionalDicesCount;
    }

    public boolean isSuccessful() {
        return count(diceRolls, diceRoll -> diceRoll.getScore() != DiceRoll.Score.BAD) + scoreBonus >= minScoreToBeSuccessful;
    }

    public int getScore() {
        int returnValue = 0;
        for (DiceRoll diceRoll : diceRolls) {
            returnValue += diceRoll.getScore().getValue();
        }
        return returnValue + scoreBonus;
    }

    public int calculateDicePool() {
        int baseStatValue = getBaseStatValue();
        int bonusStatValue = getBonusStatValue();
        int modifier = getModifier();
        int additionalDicesCount = getAdditionalDicesCount();
        int assetStatBonus = 0;
        int assetDicePoolBonus = 0;
        for (UsableAsset usableAsset : getSelectedUsableAssets()) {
            if (usableAsset.getStatBonus() > assetStatBonus) {
                assetStatBonus = usableAsset.getStatBonus();
            }
            assetDicePoolBonus += usableAsset.getDicePoolBonus();
        }
        return Math.max(minimumRolledDice, baseStatValue + bonusStatValue + modifier + assetStatBonus + assetDicePoolBonus + additionalDicesCount);
    }

    public int getSuccessRolls() {
        return count(diceRolls, diceRoll -> diceRoll.getScore() != DiceRoll.Score.BAD);
    }

    public boolean hasRolledAny4() {
        return Stream.anyMatch(getDiceRolls(), diceRoll -> diceRoll.getDiceValue() == 4);
    }

    public boolean hasRolledAny1() {
        return Stream.anyMatch(getDiceRolls(), diceRoll -> diceRoll.getDiceValue() == 1);
    }

    public boolean hasReachedMinScoreToEndTest() {
        return getScore() >= minScoreToEndTest;
    }

    public boolean isCombat() {
        return isCombat;
    }

    public void setCombat(boolean combat) {
        isCombat = combat;
    }

    public void setScoreBonus(int scoreBonus) {
        this.scoreBonus = scoreBonus;
    }

    public int getScoreBonus() {
        return scoreBonus;
    }

    public void setMinScoreToBeSuccessful(int minScoreToBeSuccessful) {
        this.minScoreToBeSuccessful = minScoreToBeSuccessful;
    }
}
