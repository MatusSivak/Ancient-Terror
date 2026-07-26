package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class HolyWaterAsset extends AbstractAssetInfo {

    public HolyWaterAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public AssetId getId() {
        return AssetId.HOLY_WATER;
    }

    @Override
    public String getName() {
        return "Holy Water";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "ACTION: You may discard this card to choose an investigator on your space.\n" +
                "That investigator gains a Blessed Condition.\n\n" +
                "You may discard this card to gain +5 Will and +5 Strength during a Combat Encounter.";
    }
}
