package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class CarbineRifleAsset extends AbstractAssetInfo {

    public CarbineRifleAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.CARBINE_RIFLE;
    }

    @Override
    public String getName() {
        return "Carbine Rifle";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Once per round,\n" +
                "Combat Strength tests:\n" +
                "+5 Shifts";
    }
}
