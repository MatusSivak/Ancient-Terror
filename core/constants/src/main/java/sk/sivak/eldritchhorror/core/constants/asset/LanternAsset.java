package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class LanternAsset extends AbstractAssetInfo {

    public LanternAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.LANTERN;
    }

    @Override
    public String getName() {
        return "Lantern";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Once per round\n" +
                "+1 Shift";
    }
}
