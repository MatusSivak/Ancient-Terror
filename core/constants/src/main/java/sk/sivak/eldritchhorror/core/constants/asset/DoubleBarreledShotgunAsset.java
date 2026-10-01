package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class DoubleBarreledShotgunAsset extends AbstractAssetInfo {

    public DoubleBarreledShotgunAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.DOUBLE_BARRELED_SHOTGUN;
    }

    @Override
    public String getName() {
        return "Double-barreled Shotgun";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "Combat: +4 Strength.\n" +
                "\n" +
                "Combat Strength tests:\n" +
                "each 6 counts as 2 successes.";
    }
}
