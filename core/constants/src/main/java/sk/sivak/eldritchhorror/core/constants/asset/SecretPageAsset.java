package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class SecretPageAsset extends AbstractAssetInfo {

    public SecretPageAsset() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.SECRET_PAGE;
    }

    @Override
    public String getName() {
        return "Secret Page";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Spell effects: +1 Lore.\n" +
                "\n" +
                "When gained from the deck\n" +
                "or reserve → gain 1 Spell.";
    }
}
