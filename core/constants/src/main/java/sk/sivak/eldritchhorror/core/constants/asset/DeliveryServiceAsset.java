package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class DeliveryServiceAsset extends AbstractAssetInfo {

    public DeliveryServiceAsset() {
        traits.add(AssetTrait.SERVICE);
        traits.add(AssetTrait.TEAMWORK);
    }

    @Override
    public AssetId getId() {
        return AssetId.DELIVERY_SERVICE;
    }

    @Override
    public String getName() {
        return "Delivery Service";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "immediately give any number\n" +
                "of Item possessions to another investigator\n" +
                "on any space.\n" +
                "Then discard this card.";
    }
}
