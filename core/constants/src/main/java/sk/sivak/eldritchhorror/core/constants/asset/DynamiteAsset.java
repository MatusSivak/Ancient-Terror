package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class DynamiteAsset extends AbstractAssetInfo {

    public DynamiteAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.DYNAMITE;
    }

    @Override
    public String getName() {
        return "Dynamite";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "ACTION: Discard this card →\n" +
                "each Monster on your space\n" +
                "loses 3 Health.";
    }
}
