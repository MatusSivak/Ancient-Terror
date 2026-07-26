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
        return "You may add 1 to the result of 1 die\n" +
                "when resolving a Will test\n" +
                "during a Combat Encounter.";
    }
}
