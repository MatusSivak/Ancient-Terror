package sk.sivak.eldritchhorror.core.constants.asset;

public class FineJewelryAsset extends AbstractAssetInfo {

    public FineJewelryAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.FINE_JEWELRY;
    }

    @Override
    public String getName() {
        return "Fine Jewelry";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Acquire Assets action:\n" +
                "add 1 to the result of 1 die.";
    }
}
