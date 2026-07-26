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
        return "When you perform a Rest action,\n" +
                "you may attempt to open the puzzle box\n" +
                "(Test Observation -2).\n" +
                "If you pass, you may\n" +
                "discard this card to gain 1 Artifact.";
    }
}
