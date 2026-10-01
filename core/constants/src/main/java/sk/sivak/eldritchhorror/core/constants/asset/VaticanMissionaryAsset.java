package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class VaticanMissionaryAsset extends AbstractAssetInfo {

    public VaticanMissionaryAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.VATICAN_MISSIONARY;
    }

    @Override
    public String getName() {
        return "Vatican Missionary";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "+1 Will.\n" +
                "\n" +
                "Will tests: reroll 1 die.";
    }
}
