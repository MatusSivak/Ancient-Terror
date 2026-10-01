package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class BandagesAsset extends AbstractAssetInfo {

    public BandagesAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.BANDAGES;
    }

    @Override
    public String getName() {
        return "Bandages";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Discard this card → prevent up to\n" +
                "2 Health loss for an investigator\n" +
                "on your space.";
    }
}
