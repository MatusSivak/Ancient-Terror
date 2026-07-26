package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class MineralogyResearchAsset extends AbstractAssetInfo {

    public MineralogyResearchAsset() {
        traits.add(AssetTrait.TASK);
    }

    @Override
    public AssetId getId() {
        return AssetId.MINERALOGY_RESEARCH;
    }

    @Override
    public String getName() {
        return "Mineralogy Research";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "After resolving a General Encounter\n" +
                "or an Expedition Encounter\n" +
                "on a Wilderness space,\n" +
                "you may examine the area's soil\n" +
                "(Test Observation).\n" +
                "If you pass, gain two Clues\n" +
                "and discard this card.";
    }
}
