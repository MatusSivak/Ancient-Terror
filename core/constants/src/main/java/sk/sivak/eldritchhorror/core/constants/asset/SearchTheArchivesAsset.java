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
        return "When gained: gain 1 Tome\n" +
                "from the deck.";
    }
}
