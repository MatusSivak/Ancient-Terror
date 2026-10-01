package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ElderSignAsset extends AbstractAssetInfo {

    public ElderSignAsset() {
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.ELDER_SIGN;
    }

    @Override
    public String getName() {
        return "Elder Sign";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Combat Will tests: reroll 1 die.\n" +
                "\n" +
                "greenMinus1 Monster Horror (min. 1)";
    }
}
