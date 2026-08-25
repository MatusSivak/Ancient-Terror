package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class TearGasAsset extends AbstractAssetInfo {

    public TearGasAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.TEAR_GAS;
    }

    @Override
    public String getName() {
        return "Tear Gas";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Combat Strength tests:\n" +
                "Discard → +2 Rerolls\n" +
                "greenMinus2 Monster Damage (min. 1)";
    }
}
