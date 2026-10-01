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
        return "Combat Will tests: reroll 1 die.\n" +
                "\n" +
                "Rest: recover 1 extra Sanity.";
    }
}
