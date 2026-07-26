package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class LuckyRingAsset extends AbstractAssetInfo {

    public LuckyRingAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.LUCKY_RING;
    }

    @Override
    public String getName() {
        return "Lucky Ring";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "You may reroll 1 die\nwhen resolving a test.";
    }
}
