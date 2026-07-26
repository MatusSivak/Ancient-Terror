package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class PrivateInvestigatorAsset extends AbstractAssetInfo {

    public PrivateInvestigatorAsset() {
        traits.add(AssetTrait.ALLY);
    }

    @Override
    public AssetId getId() {
        return AssetId.PRIVATE_INVESTIGATOR;
    }

    @Override
    public String getName() {
        return "Private Investigator";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Gain +1 Observation.\n" +
                "\n" +
                "You may reroll 1 die when resolving\n" +
                "an Observation test.";
    }
}
