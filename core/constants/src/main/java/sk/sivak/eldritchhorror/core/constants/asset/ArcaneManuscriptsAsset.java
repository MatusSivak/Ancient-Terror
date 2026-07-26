package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ArcaneManuscriptsAsset extends AbstractAssetInfo {

    public ArcaneManuscriptsAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.ARCANE_MANUSCRIPTS;
    }

    @Override
    public String getName() {
        return "Arcane Manuscripts";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Gain +1 Lore when resolving Spell effects.";
    }
}
