package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class HolyCrossAsset extends AbstractAssetInfo {

    public HolyCrossAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.HOLY_CROSS;
    }

    @Override
    public String getName() {
        return "Holy Cross";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Gain +2 Will during Combat Encounters.";
    }
}
