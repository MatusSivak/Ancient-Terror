package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class SanctuaryAsset extends AbstractAssetInfo {

    public SanctuaryAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.SANCTUARY;
    }

    @Override
    public String getName() {
        return "Sanctuary";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "you may immediately\n" +
                "discard 1 Condition.\n" +
                "Then discard this card.";
    }
}
