package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class TomeOfSecretsAsset extends AbstractAssetInfo {

    public TomeOfSecretsAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.TOME_OF_SECRETS;
    }

    @Override
    public String getName() {
        return "Tome of Secrets";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Once per round, you may spend 1 Focus in place of spending 1 Clue.\n" +
                "\n" +
                "When you perform a Focus action, recover 1 Sanity.";
    }
}
