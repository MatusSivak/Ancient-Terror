package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class GruesomeTalismanAsset extends AbstractAssetInfo {

    public GruesomeTalismanAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.GRUESOME_TALISMAN;
    }

    @Override
    public String getName() {
        return "Gruesome Talisman";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Combat Will tests:\n" +
                "+1 Lift";
    }
}
