package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class NewspaperReportAsset extends AbstractAssetInfo {

    public NewspaperReportAsset() {
        traits.add(AssetTrait.TASK);
    }

    @Override
    public AssetId getId() {
        return AssetId.NEWSPAPER_REPORT;
    }

    @Override
    public String getName() {
        return "Newspaper Report";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "After a Research Encounter:\n" +
                "spend 1 Clue gained there and\n" +
                "discard this card → retreat Doom.";
    }
}
