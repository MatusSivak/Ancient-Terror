package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class ExpeditionMapAsset extends AbstractAssetInfo {

    public ExpeditionMapAsset() {
        traits.add(AssetTrait.TRINKET);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public AssetId getId() {
        return AssetId.EXPEDITION_MAP;
    }

    @Override
    public String getName() {
        return "Expedition Map";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Once per round,\n" +
                "you may add 1 to the result of 1 die\n" +
                "when resolving a test during\n" +
                "an Expedition Encounter";
    }
}
