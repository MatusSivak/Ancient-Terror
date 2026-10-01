package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class HiredMuscleAsset extends AbstractAssetInfo {

    public HiredMuscleAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.HIRED_MUSCLE;
    }

    @Override
    public String getName() {
        return "Hired Muscle";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "+1 Strength.\n" +
                "\n" +
                "Strength tests: reroll 1 die.";
    }
}
