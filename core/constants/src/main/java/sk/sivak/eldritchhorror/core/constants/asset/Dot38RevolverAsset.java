package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class Dot38RevolverAsset extends AbstractAssetInfo {

    public Dot38RevolverAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.DOT_38_REVOLVER;
    }

    @Override
    public String getName() {
        return ".38 Revolver";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Combat Strength tests:\n" +
                "+2 Swaps";
    }
}
