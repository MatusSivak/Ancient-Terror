package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class TomeOfHorrorsAsset extends AbstractAssetInfo {

    public TomeOfHorrorsAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.TOME_OF_HORRORS;
    }

    @Override
    public String getName() {
        return "Tome of Horrors";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Gain +2 Will during Combat Encounters.\n" +
                "\n" +
                "Reduce the horror of Monsters\n" +
                "you encounter by 1, to a minimum of 1.";
    }
}
