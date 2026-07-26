package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class KeroseneAsset extends AbstractAssetInfo {

    public KeroseneAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.KEROSENE;
    }

    @Override
    public String getName() {
        return "Kerosene";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "You may discard this card\n" +
                "to gain +5 Strength\n" +
                "during a Combat Encounter.";
    }
}
