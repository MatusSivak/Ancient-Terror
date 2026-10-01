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
        return "After a General or Expedition\n" +
                "Encounter on a Wilderness space:\n" +
                "you may test Observation.\n" +
                "Pass → gain 2 Clues, discard this card.";
    }
}
