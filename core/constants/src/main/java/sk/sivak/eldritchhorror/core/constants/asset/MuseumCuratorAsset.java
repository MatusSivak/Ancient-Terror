package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class MuseumCuratorAsset extends AbstractAssetInfo {

    public MuseumCuratorAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.MUSEUM_CURATOR;
    }

    @Override
    public String getName() {
        return "Museum Curator";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "You may reroll 1 die\n" +
                "when resolving a test during\n" +
                "an Expedition Encounter.";
    }
}
