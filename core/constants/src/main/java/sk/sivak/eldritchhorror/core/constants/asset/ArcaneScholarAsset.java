package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ArcaneScholarAsset extends AbstractAssetInfo {

    public ArcaneScholarAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.ARCANE_SCHOLAR;
    }

    @Override
    public String getName() {
        return "Arcane Scholar";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Gain +1 Lore.\n" +
                "\n" +
                "You may reroll 1 die when resolving a Lore test.";
    }
}
