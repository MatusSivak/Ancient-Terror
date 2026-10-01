package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class MysteriousTomeAsset extends AbstractAssetInfo {

    public MysteriousTomeAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.MYSTERIOUS_TOME;
    }

    @Override
    public String getName() {
        return "Mysterious Tome";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Lore and Will tests:\n" +
                "when you spend Focus to reroll,\n" +
                "reroll up to 2 dice instead.";
    }
}
