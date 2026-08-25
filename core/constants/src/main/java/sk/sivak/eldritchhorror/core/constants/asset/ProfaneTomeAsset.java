package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ProfaneTomeAsset extends AbstractAssetInfo {

    public ProfaneTomeAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.PROFANE_TOME;
    }

    @Override
    public String getName() {
        return "Profane Tome";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Combat Will tests:\n" +
                "+1 Reroll";
    }
}
