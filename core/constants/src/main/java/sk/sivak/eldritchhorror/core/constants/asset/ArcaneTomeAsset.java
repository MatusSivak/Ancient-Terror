package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ArcaneTomeAsset extends AbstractAssetInfo {

    public ArcaneTomeAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.ARCANE_TOME;
    }

    @Override
    public String getName() {
        return "Arcane Tome";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Gain +2 Lore when \n" +
                "resolving Spell effects.\n" +
                "\n" +
                "When you perform a Rest action,\n" +
                "you may test Lore.\n" +
                "If you pass, gain 1 Spell.";
    }
}
