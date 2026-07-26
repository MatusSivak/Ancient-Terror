package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class LuckyCigaretteCaseAsset extends AbstractAssetInfo {

    public LuckyCigaretteCaseAsset() {
        traits.add(AssetTrait.TRINKET);
    }

    @Override
    public AssetId getId() {
        return AssetId.LUCKY_CIGARETTE_CASE;
    }

    @Override
    public String getName() {
        return "Lucky Cigarette Case";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Once per round, you may\n" +
                "add 1 to the result of 1 die\n" +
                "when resolving a test.";
    }
}
