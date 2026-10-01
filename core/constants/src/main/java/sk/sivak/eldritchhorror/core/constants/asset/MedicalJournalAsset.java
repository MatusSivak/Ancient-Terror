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
        return "Illness and Injury Condition\n" +
                "Strength tests: reroll 1 die.\n" +
                "\n" +
                "Rest: recover 1 extra Health.";
    }
}
