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
        return "Once per round: pay 1 less Clue\n" +
                "for an effect.\n" +
                "\n" +
                "Other World Encounter tests:\n" +
                "reroll 1 die.";
    }
}
