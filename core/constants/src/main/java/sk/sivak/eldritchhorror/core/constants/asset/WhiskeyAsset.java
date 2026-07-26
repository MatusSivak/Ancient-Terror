package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class WhiskeyAsset extends AbstractAssetInfo {

    public WhiskeyAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.WHISKEY;
    }

    @Override
    public String getName() {
        return "Whiskey";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "You may discard this card\n" +
                "to prevent an investigator\n" +
                "on your space from\n" +
                "losing up to 2 Sanity.";
    }
}
