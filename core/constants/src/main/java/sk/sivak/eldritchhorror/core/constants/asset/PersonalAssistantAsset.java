package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class PersonalAssistantAsset extends AbstractAssetInfo {

    public PersonalAssistantAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.PERSONAL_ASSISTANT;
    }

    @Override
    public String getName() {
        return "Personal Assistant";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "+1 Influence.\n" +
                "\n" +
                "Influence tests: reroll 1 die.";
    }
}
