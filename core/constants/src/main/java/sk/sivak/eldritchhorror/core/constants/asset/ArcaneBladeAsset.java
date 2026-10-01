package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ArcaneBladeAsset extends AbstractAssetInfo {

    public ArcaneBladeAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.ARCANE_BLADE;
    }

    @Override
    public String getName() {
        return "Arcane Blade";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Combat: +2 Strength.\n" +
                "Spell effects: +2 Lore.";
    }
}
