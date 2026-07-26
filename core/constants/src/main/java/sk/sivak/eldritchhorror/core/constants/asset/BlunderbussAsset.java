package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class BlunderbussAsset extends AbstractAssetInfo {

    public BlunderbussAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.BLUNDERBUSS;
    }

    @Override
    public String getName() {
        return "Blunderbuss";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "When resolving a Combat Encounter,\n" +
                "you may gain +2 Strength.\n\n" +
                "If you do, each 6 you roll\n" +
                "counts as 2 successes\n" +
                "and each 1 negates 1 success.";
    }
}
