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
        return "Once per round: spend 1 Focus\n" +
                "instead of 1 Clue.\n" +
                "\n" +
                "Focus action: recover 1 Sanity.";
    }
}
