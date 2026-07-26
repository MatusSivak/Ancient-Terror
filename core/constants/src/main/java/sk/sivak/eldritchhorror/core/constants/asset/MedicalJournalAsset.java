package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class MedicalJournalAsset extends AbstractAssetInfo {

    public MedicalJournalAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.MEDICAL_JOURNAL;
    }

    @Override
    public String getName() {
        return "Medical Journal";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "You may reroll 1 die when resolving\n" +
                "a Strength test as part of\n" +
                "an Illness or Injury Condition effect.\n" +
                "\n" +
                "When you perform a Rest action, recover 1 additional Health";
    }
}
