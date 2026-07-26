package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class HandcuffsAsset extends AbstractAssetInfo {

    public HandcuffsAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.HANDCUFFS;
    }

    @Override
    public String getName() {
        return "Handcuffs";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Before resolving the Strength test during a Combat Encounter,\n" +
                "you may spend 1 Focus to defeat that Monster if it has toughness 2 or less.";
    }
}
