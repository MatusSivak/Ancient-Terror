package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class LuckyRabbitsFootAsset extends AbstractAssetInfo {

    public LuckyRabbitsFootAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.LUCKY_RABBITS_FOOT;
    }

    @Override
    public String getName() {
        return "Lucky Rabbit's Foot";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Once per round\n" +
                "+1 Reroll";
    }
}
