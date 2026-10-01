package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class FishingNetAsset extends AbstractAssetInfo {

    public FishingNetAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.FISHING_NET;
    }

    @Override
    public String getName() {
        return "Fishing Net";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Combat Strength tests: reroll 1 die.\n" +
                "\n" +
                "greenMinus1 Monster Damage (min. 1)";
    }
}
