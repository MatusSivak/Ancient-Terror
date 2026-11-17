package sk.sivak.eldritchhorror.core.constants.asset;

public class LuckyTalismanAsset extends AbstractAssetInfo {

    public LuckyTalismanAsset() {
        traits.add(AssetTrait.TRINKET);
        traits.add(AssetTrait.RELIC);
    }

    @Override
    public AssetId getId() {
        return AssetId.LUCKY_TALISMAN;
    }

    @Override
    public String getName() {
        return "Lucky Talisman";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Once per round, you may reroll all of your dice when resolving a test.";
    }
}
