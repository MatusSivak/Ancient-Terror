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
        return "When you defeat a Monster\n" +
                "with toughness two or more\n" +
                "during a Combat Encounter,\n" +
                "you may examine the creature's remains (Test Observation).\n" +
                "If you pass, gain 2 Clues\n" +
                "and discard this card.";
    }
}
