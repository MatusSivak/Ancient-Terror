package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class OpusArcanaAsset extends AbstractAssetInfo {

    public OpusArcanaAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.OPUS_ARCANA;
    }

    @Override
    public String getName() {
        return "Opus Arcana";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "+1 Lore.\n" +
                "Spell effects: +2 Lore.";
    }
}
