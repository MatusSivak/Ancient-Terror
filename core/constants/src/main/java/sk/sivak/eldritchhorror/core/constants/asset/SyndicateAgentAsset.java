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
        return "Gain +2 Strength\n" +
                "during Combat Encounters.\n" +
                "\n" +
                "You may reroll 1 die when\n" +
                "resolving a Strength test\n" +
                "during a Combat Encounter.";
    }
}
