package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class EnchantedBladeAsset extends AbstractAssetInfo {

    public EnchantedBladeAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.ENCHANTED_BLADE;
    }

    @Override
    public String getName() {
        return "Enchanted Blade";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Gain +1 Will and +3 Strength\n" +
                "during Combat Encounters.";
    }
}
