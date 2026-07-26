package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ConsecrationAsset extends AbstractAssetInfo {

    public ConsecrationAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.CONSECRATION;
    }

    @Override
    public String getName() {
        return "Consecration";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "immediately gain 1 Boon Condition.\n" +
                "Then discard this card.";
    }
}
