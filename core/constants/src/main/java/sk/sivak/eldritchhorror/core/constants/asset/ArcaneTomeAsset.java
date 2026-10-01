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
        return "Spell effects: +2 Lore.\n" +
                "\n" +
                "Rest: you may test Lore.\n" +
                "Pass → gain 1 Spell.";
    }
}
