package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class RitualDaggerAsset extends AbstractAssetInfo {

    public RitualDaggerAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public AssetId getId() {
        return AssetId.RITUAL_DAGGER;
    }

    @Override
    public String getName() {
        return "Ritual Dagger";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Once per round, you may gain +1 Lore\n" +
                "when resolving a Spell effect\n" +
                "or +2 Strength when\n" +
                "resolving a Combat Encounter.";
    }
}
