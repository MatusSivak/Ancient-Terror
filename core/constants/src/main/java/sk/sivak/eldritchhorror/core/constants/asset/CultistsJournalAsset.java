package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class CultistsJournalAsset extends AbstractAssetInfo {

    public CultistsJournalAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public AssetId getId() {
        return AssetId.CULTISTS_JOURNAL;
    }

    @Override
    public String getName() {
        return "Cultist's Journal";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Gain +2 Will and +2 Strength\n" +
                "during Combat Encounters.";
    }
}
