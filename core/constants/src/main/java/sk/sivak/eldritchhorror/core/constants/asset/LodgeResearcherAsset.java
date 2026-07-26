package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class LodgeResearcherAsset extends AbstractAssetInfo {

    public LodgeResearcherAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.LODGE_RESEARCHER;
    }

    @Override
    public String getName() {
        return "Lodge Researcher";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "If you defeat a Monster\n" +
                "during a Combat Encounter,\n" +
                "recover 1 Sanity and gain 1 Clue.";
    }
}
