package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class SilverTwilightRitualAsset extends AbstractAssetInfo {

    public SilverTwilightRitualAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.SILVER_TWILIGHT_RITUAL;
    }

    @Override
    public String getName() {
        return "Silver Twilight Ritual";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "immediately retreat Doom.\n" +
                "Then discard this card.";
    }
}
