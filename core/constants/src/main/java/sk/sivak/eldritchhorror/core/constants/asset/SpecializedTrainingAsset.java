package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class SpecializedTrainingAsset extends AbstractAssetInfo {

    public SpecializedTrainingAsset() {
        traits.add(AssetTrait.TASK);
    }

    @Override
    public AssetId getId() {
        return AssetId.SPECIALIZED_TRAINING;
    }

    @Override
    public String getName() {
        return "Specialized Training";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Focus action: you may test Will.\n" +
                "Pass: Discard this card →\n" +
                "improve 1 skill of your choice.";
    }
}
