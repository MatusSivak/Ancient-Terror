package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class FineClothesAsset extends AbstractAssetInfo {

    public FineClothesAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.FINE_CLOTHES;
    }

    @Override
    public String getName() {
        return "Fine Clothes";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Each 6 you roll when performing\n" +
                "an Acquire Assets action\n" +
                "counts as 2 successes.";
    }
}
