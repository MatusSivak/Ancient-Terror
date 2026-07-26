package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class UrbanGuideAsset extends AbstractAssetInfo {

    public UrbanGuideAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.URBAN_GUIDE;
    }

    @Override
    public String getName() {
        return "Urban Guide";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "If you are on a City space, " +
                "investigators on your space roll 1 additional die " +
                "when resolving tests " +
                "except when resolving Other World Encounters.";
    }
}
