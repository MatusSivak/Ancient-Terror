package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ProtectiveAmuletAsset extends AbstractAssetInfo {

    public ProtectiveAmuletAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.PROTECTIVE_AMULET;
    }

    @Override
    public String getName() {
        return "Protective Amulet";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Combat Will tests:\n" +
                "+1 Swap";
    }
}
