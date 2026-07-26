package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class Dot18DerringerAsset extends AbstractAssetInfo {

    public Dot18DerringerAsset() {
        traits.add(AssetTrait.TRINKET);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.DOT_18_DERRINGER;
    }

    @Override
    public String getName() {
        return ".18 Derringer";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "You may add 1 to the result of 1 die\n" +
                "when resolving a Strength test during a Combat Encounter.";
    }
}
