package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class AgencyQuarantineAsset extends AbstractAssetInfo {

    public AgencyQuarantineAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.AGENCY_QUARANTINE;
    }

    @Override
    public String getName() {
        return "Agency Quarantine";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "immediately choose a space.\n" +
                "Each Monster on the chosen space\n" +
                "loses 4 Health.\n" +
                "Then discard this card.";
    }
}
