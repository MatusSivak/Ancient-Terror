package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class SearchTheArchivesAsset extends AbstractAssetInfo {

    public SearchTheArchivesAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.SEARCH_THE_ARCHIVES;
    }

    @Override
    public String getName() {
        return "Search the Archives";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "When you gain this card,\n" +
                "immediately gain one Tome\n" +
                "from the deck.";
    }
}
