package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class PocketWatchAsset extends AbstractAssetInfo {

    public PocketWatchAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.POCKET_WATCH;
    }

    @Override
    public String getName() {
        return "Pocket Watch";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "You cannot become Delayed\n" +
                "unless you choose to.";
    }
}
