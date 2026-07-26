package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class HolySpearAsset extends AbstractAssetInfo {

    public HolySpearAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.RELIC);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.HOLY_SPEAR;
    }

    @Override
    public String getName() {
        return "Holy Spear";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Gain +4 Strength\n" +
                "during Combat Encounters.";
    }
}
