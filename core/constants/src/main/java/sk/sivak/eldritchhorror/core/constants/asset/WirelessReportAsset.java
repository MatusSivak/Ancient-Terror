package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class WirelessReportAsset extends AbstractAssetInfo {

    public WirelessReportAsset() {
        traits.add(AssetTrait.SERVICE);
        traits.add(AssetTrait.TEAMWORK);
    }

    @Override
    public AssetId getId() {
        return AssetId.WIRELESS_REPORT;
    }

    @Override
    public String getName() {
        return "Wireless Report";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "immediately give any number of Clues\n" +
                "to another investigator on any space.\n" +
                "Then discard this card.";
    }
}
