package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class FreshFruitAsset extends AbstractAssetInfo {

    public FreshFruitAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.FRESH_FRUIT;
    }

    @Override
    public String getName() {
        return "Fresh Fruit";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "When you perform a Rest action, you may discard this card to recover 1 additional Health and 1 additional Sanity.";
    }
}
