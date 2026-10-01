package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class PrivateCareAsset extends AbstractAssetInfo {

    public PrivateCareAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.PRIVATE_CARE;
    }

    @Override
    public String getName() {
        return "Private Care";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "When gained: recover all Health\n" +
                "and Sanity.\n" +
                "Then discard this card.";
    }
}
