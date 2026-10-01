package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class AxeAsset extends AbstractAssetInfo {

    public AxeAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.AXE;
    }

    @Override
    public String getName() {
        return "Axe";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Combat: +2 Strength.\n" +
                "\n" +
                "Combat Strength tests:\n" +
                "2 Sanity → reroll any dice.";
    }
}
