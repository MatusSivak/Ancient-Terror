package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class BlunderbussAsset extends AbstractAssetInfo {

    public BlunderbussAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.BLUNDERBUSS;
    }

    @Override
    public String getName() {
        return "Blunderbuss";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Combat: you may gain +2 Strength.\n" +
                "If you do, each 6 counts as\n" +
                "2 successes and each 1\n" +
                "cancels 1 success.";
    }
}
