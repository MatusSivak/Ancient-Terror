package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ElderSignAsset extends AbstractAssetInfo {

    public ElderSignAsset() {
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.ELDER_SIGN;
    }

    @Override
    public String getName() {
        return "Elder Sign";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "You may reroll 1 die\n" +
                "when resolving a Will test\n" +
                "during a Combat Encounter.\n" +
                "\n" +
                "Reduce the Horror of Monsters\n" +
                "you encounter by 1 to a minimum of 1.";
    }
}
