package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class GamblersDiceAsset extends AbstractAssetInfo {

    public GamblersDiceAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.GAMBLERS_DICE;
    }

    @Override
    public String getName() {
        return "Gambler's Dice";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "You roll a minimum of two dice\n" +
                "when resolving tests.\n" +
                "\n" +
                "Once per round,\n" +
                "you may reroll two dice\n" +
                "with matching results\n" +
                "when resolving a test.";
    }
}
