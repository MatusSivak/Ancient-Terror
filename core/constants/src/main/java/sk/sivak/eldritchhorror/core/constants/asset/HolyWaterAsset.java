package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class HolyWaterAsset extends AbstractAssetInfo {

    public HolyWaterAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public AssetId getId() {
        return AssetId.HOLY_WATER;
    }

    @Override
    public String getName() {
        return "Holy Water";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "ACTION: Discard this card → 1 investigator\n" +
                "on your space gains Blessed.\n" +
                "\n" +
                "Combat: Discard this card →\n" +
                "+5 Will and +5 Strength.";
    }
}
