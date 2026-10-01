package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ResearchStudentAsset extends AbstractAssetInfo {

    public ResearchStudentAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.RESEARCH_STUDENT;
    }

    @Override
    public String getName() {
        return "Research Student";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "ACTION: Roll 1 die.\n" +
                "5 or 6 → gain 1 Clue.\n" +
                "1 → discard the nearest Clue.";
    }
}
