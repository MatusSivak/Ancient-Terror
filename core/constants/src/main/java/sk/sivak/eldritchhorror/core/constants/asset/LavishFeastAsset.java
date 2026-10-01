package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class LavishFeastAsset extends AbstractAssetInfo {

    public LavishFeastAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.LAVISH_FEAST;
    }

    @Override
    public String getName() {
        return "Lavish Feast";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "When gained: recover 2 Health\n" +
                "and 2 Sanity.\n" +
                "Then discard this card.";
    }
}
