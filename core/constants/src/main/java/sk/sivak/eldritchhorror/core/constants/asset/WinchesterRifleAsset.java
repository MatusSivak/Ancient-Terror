package sk.sivak.eldritchhorror.core.constants.asset;

public class WinchesterRifleAsset extends AbstractAssetInfo {

    public WinchesterRifleAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.WINCHESTER_RIFLE;
    }

    @Override
    public String getName() {
        return "Winchester Rifle";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Once per round\n" +
                "Combat: +4 Strength.";
    }
}
