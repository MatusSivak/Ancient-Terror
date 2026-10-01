package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class PoliceAssistanceAsset extends AbstractAssetInfo {

    public PoliceAssistanceAsset() {
        traits.add(AssetTrait.SERVICE);
    }

    @Override
    public AssetId getId() {
        return AssetId.POLICE_ASSISTANCE;
    }

    @Override
    public String getName() {
        return "Police Assistance";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "When gained: discard 1 Monster with\n" +
                "toughness 2 or less on any space.\n" +
                "Then discard this card.";
    }
}
