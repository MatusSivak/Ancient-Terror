package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class CatBurglarAsset extends AbstractAssetInfo {

    public CatBurglarAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.CAT_BURGLAR;
    }

    @Override
    public String getName() {
        return "Cat Burglar";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "ACTION: Roll 1 die.\n" +
                "5 or 6 → gain 1 Item or Trinket\n" +
                "Asset from the reserve.\n" +
                "1 → discard this card.";
    }
}
