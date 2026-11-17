package sk.sivak.eldritchhorror.core.constants.asset;

public class AncientTomeAsset extends AbstractAssetInfo {

    public AncientTomeAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.RELIC);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.ANCIENT_TOME;
    }

    @Override
    public String getName() {
        return "Ancient Tome";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Action: Test Lore. If you pass, you may spend 1 Sanity to gain 1 Clue or 1 Spell.";
    }
}
