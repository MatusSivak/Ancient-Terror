package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class TheSilverKeyArtifact extends AbstractArtifactInfo {

    public TheSilverKeyArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.THE_SILVER_KEY;
    }

    @Override
    public String getName() {
        return "The Silver Key";
    }

    @Override
    public String getDescription() {
        return "Once per round, you may spend\n" +
                "1 less Clue to pay for an effect.\n" +
                "\n" +
                "You may reroll 1 die when resolving a test\n" +
                "during an Other World Encounter.";
    }
}
