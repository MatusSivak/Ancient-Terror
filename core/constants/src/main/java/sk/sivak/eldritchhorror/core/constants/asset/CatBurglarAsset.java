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
        return "ACTION: Roll 1 die. On a 5 or 6,\n" +
                "gain 1 Item or Trinket Asset\n" +
                "from the reserve.\n" +
                "On a 1, discard this card.";
    }
}
