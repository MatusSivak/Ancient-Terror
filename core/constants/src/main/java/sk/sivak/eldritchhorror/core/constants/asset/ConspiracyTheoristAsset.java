package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ConspiracyTheoristAsset extends AbstractAssetInfo {

    public ConspiracyTheoristAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.CONSPIRACY_THEORIST;
    }

    @Override
    public String getName() {
        return "Conspiracy Theorist";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Gain +2 Observation during Research Encounters.\n" +
                "\n" +
                "RECKONING: Roll 1 die. On a 4, 5, or 6, gain 1 Clue.";
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }
}
