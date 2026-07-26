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
        return "When you gain this card,\n" +
                "immediately discard 1 Monster\n" +
                "of your choice on any space\n" +
                "with toughness 2 or less.\n" +
                "Then discard this card.";
    }
}
