package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class Dot45ColtRevolverAsset extends AbstractAssetInfo {

    public Dot45ColtRevolverAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.DOT_45_COLT_REVOLVER;
    }

    @Override
    public String getName() {
        return ".45 Colt Revolver";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Once per round\n" +
                "Combat Strength tests:\n" +
                "+3 Shifts";
    }
}
