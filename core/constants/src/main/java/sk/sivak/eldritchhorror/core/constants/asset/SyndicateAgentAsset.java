package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class SyndicateAgentAsset extends AbstractAssetInfo {

    public SyndicateAgentAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.SYNDICATE_AGENT;
    }

    @Override
    public String getName() {
        return "Syndicate Agent";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Combat: +2 Strength.\n" +
                "\n" +
                "Combat Strength tests: reroll 1 die.";
    }
}
