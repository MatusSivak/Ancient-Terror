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
        return "When you perform a Focus action,\n" +
                "you may consult experts to train you\n" +
                "(Test Will).\n" +
                "If you pass, discard this card\n" +
                "and improve 1 Skill of your choice.";
    }
}
