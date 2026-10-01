package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ExpeditionGuideAsset extends AbstractAssetInfo {

    public ExpeditionGuideAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.EXPEDITION_GUIDE;
    }

    @Override
    public String getName() {
        return "Expedition Guide";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Observation and Strength tests:\n" +
                "when you spend Focus to reroll,\n" +
                "reroll up to 2 dice instead.";
    }
}
