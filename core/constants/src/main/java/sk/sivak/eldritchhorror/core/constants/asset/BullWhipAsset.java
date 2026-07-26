package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class BullWhipAsset extends AbstractAssetInfo {

    public BullWhipAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public AssetId getId() {
        return AssetId.BULL_WHIP;
    }

    @Override
    public String getName() {
        return "Bull Whip";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Gain +1 Strength during Combat Encounters.\n" +
                "\n" +
                "You may reroll 1 die when resolving\n" +
                "a Strength test during a Combat Encounter.";
    }
}
