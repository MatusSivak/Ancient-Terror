package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class KingJamesBibleAsset extends AbstractAssetInfo {

    public KingJamesBibleAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.KING_JAMES_BIBLE;
    }

    @Override
    public String getName() {
        return "King James Bible";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "You may reroll 1 die when resolving\n" +
                "a Will test during a Combat Encounter.\n" +
                "\n" +
                "When you perform a Rest action,\n" +
                "recover 1 additional Sanity.";
    }
}
