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
        return "Discard this card → prevent up to\n" +
                "2 Sanity loss for an investigator\n" +
                "on your space.";
    }
}
