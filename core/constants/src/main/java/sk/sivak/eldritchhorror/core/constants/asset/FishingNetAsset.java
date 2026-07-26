package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class FishingNetAsset extends AbstractAssetInfo {

    public FishingNetAsset() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public AssetId getId() {
        return AssetId.FISHING_NET;
    }

    @Override
    public String getName() {
        return "Fishing Net";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "You may reroll 1 die\n" +
                "when resolving a Strength test\n" +
                "during a Combat Encounter.\n" +
                "\n" +
                "Reduce the Damage of Monsters\n" +
                "you encounter by 1 to a minimum of 1.";
    }
}
