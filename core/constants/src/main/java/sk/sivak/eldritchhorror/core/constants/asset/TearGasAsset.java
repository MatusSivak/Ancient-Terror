package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class TearGasAsset extends AbstractAssetInfo {

    public TearGasAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.TEAR_GAS;
    }

    @Override
    public String getName() {
        return "Tear Gas";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "You may discard this card to reroll up to 2 dice when resolving a Strength test during a Combat Encounter.\n" +
                "In addition, reduce the Monster's Damage by 2 to a minimum of 1.";
    }
}
