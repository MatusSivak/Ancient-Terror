package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class AgentOfSecretsAsset extends AbstractAssetInfo {

    public AgentOfSecretsAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.AGENT_OF_SECRETS;
    }

    @Override
    public String getName() {
        return "Agent of Secrets";
    }

    @Override
    public int getCost() {
        return 4;
    }

    @Override
    public String getDescription() {
        return "+1 Lore and +1 Observation.\n" +
                "\n" +
                "Lore and Observation tests:\n" +
                "reroll 1 die.";
    }
}
