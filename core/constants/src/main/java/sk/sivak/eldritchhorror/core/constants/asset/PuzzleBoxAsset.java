package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class PuzzleBoxAsset extends AbstractAssetInfo {

    public PuzzleBoxAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.PUZZLE_BOX;
    }

    @Override
    public String getName() {
        return "Puzzle Box";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Rest: you may test Observation -2.\n" +
                "Pass: Discard this card →\n" +
                "gain 1 Artifact.";
    }
}
