package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class SpiritDaggerAsset extends AbstractAssetInfo {

    public SpiritDaggerAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public AssetId getId() {
        return AssetId.SPIRIT_DAGGER;
    }

    @Override
    public String getName() {
        return "Spirit Dagger";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Gain +1 Will and +2 Strength\n" +
                "during Combat Encounters.";
    }
}
