package sk.sivak.eldritchhorror.core.constants.asset;

public class Dot25AutomaticAsset extends AbstractAssetInfo {

    public Dot25AutomaticAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.DOT_25_AUTOMATIC;
    }

    @Override
    public String getName() {
        return ".25 Automatic";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Combat: Roll 1 additional die when resolving a Strength test.";
    }
}
