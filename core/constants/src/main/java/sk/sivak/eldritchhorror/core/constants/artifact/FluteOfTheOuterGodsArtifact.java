package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class FluteOfTheOuterGodsArtifact extends AbstractArtifactInfo {

    public FluteOfTheOuterGodsArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.FLUTE_OF_THE_OUTER_GODS;
    }

    @Override
    public String getName() {
        return "Flute of the Outer Gods";
    }

    @Override
    public String getDescription() {
        return "ACTION: Spend 2 Health and 2 Sanity\n" +
                "to defeat all Monsters on your space.";
    }
}