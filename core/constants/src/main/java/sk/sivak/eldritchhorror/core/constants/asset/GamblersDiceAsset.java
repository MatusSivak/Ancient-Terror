package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class GamblersDiceAsset extends AbstractAssetInfo {

    public GamblersDiceAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.GAMBLERS_DICE;
    }

    @Override
    public String getName() {
        return "Gambler's Dice";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Tests: roll at least 2 dice.\n" +
                "\n" +
                "Once per round: reroll 2 dice\n" +
                "with matching results.";
    }
}
