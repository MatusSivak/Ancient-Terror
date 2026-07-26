package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class IntelligenceReportAsset extends AbstractAssetInfo {

    public IntelligenceReportAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.INTELLIGENCE_REPORT;
    }

    @Override
    public String getName() {
        return "Intelligence Report";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "immediately gain 2 Clues.\n" +
                "Then discard this card.";
    }
}
