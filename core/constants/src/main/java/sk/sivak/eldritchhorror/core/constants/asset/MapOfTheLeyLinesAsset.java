package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class MapOfTheLeyLinesAsset extends AbstractAssetInfo {

    public MapOfTheLeyLinesAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.MAP_OF_THE_LEY_LINES;
    }

    @Override
    public String getName() {
        return "Map of the Ley Lines";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "+1 Lore and +1 Will.\n" +
                "\n" +
                "Once per round, Other World\n" +
                "Encounter tests: reroll 1 die.";
    }
}
