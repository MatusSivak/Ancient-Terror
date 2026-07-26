package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class AstronomyGuidebookAsset extends AbstractAssetInfo {

    public AstronomyGuidebookAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.ASTRONOMY_GUIDEBOOK;
    }

    @Override
    public String getName() {
        return "Astronomy Guidebook";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "When you close a Gate\n" +
                "during an Other World Encounter,\n" +
                "recover one Sanity and gain one Clue.";
    }
}
