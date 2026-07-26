package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class WitchDoctorAsset extends AbstractAssetInfo {

    public WitchDoctorAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.WITCH_DOCTOR;
    }

    @Override
    public String getName() {
        return "Witch Doctor";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Investigators on your space may recover 1 additional Health\n" +
                "or\n" +
                "discard a Cursed Condition when performing a Rest action.";
    }
}
