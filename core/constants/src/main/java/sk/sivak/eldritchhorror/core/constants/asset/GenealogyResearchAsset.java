package sk.sivak.eldritchhorror.core.constants.asset;

/**
 * @author msivak
 */
public class GenealogyResearchAsset extends AbstractAssetInfo {

    public GenealogyResearchAsset() {
        traits.add(AssetTrait.TASK);
    }

    @Override
    public AssetId getId() {
        return AssetId.GENEALOGY_RESEARCH;
    }

    @Override
    public String getName() {
        return "Genealogy Research";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Defeat a Monster with toughness\n" +
                "2 or more in combat → you may\n" +
                "test Observation.\n" +
                "Pass → gain 2 Clues, discard this card.";
    }
}
