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
        return "Before the Combat Strength test:\n" +
                "1 Focus → defeat the Monster\n" +
                "if its toughness is 2 or less.";
    }
}
