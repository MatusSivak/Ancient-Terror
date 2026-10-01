package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class BodyguardAsset extends AbstractAssetInfo {

    public BodyguardAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.BODYGUARD;
    }

    @Override
    public String getName() {
        return "Bodyguard";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "+1 Strength.\n" +
                "\n" +
                "greenMinus1 Monster Damage (min. 1)";
    }
}
