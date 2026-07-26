package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class AxeAsset extends AbstractAssetInfo {

    public AxeAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.AXE;
    }

    @Override
    public String getName() {
        return "Axe";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Gain +2 Strength during Combat Encounters.\n" +
                "\n" +
                "You may spend 2 Sanity to reroll any number of dice when resolving a Strength test during a Combat Encounter.";
    }
}
