package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class CharterFlightAsset extends AbstractAssetInfo {

    public CharterFlightAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.CHARTER_FLIGHT;
    }

    @Override
    public String getName() {
        return "Charter Flight";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "immediately move up to 2 spaces.\n" +
                "Then discard this card.";
    }
}
