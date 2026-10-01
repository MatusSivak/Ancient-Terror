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
        return "When gained: give any number of\n" +
                "Item possessions to another\n" +
                "investigator on any space.\n" +
                "Then discard this card.";
    }
}
