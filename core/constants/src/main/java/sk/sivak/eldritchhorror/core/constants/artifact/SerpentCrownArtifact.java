package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class SerpentCrownArtifact extends AbstractArtifactInfo {

    public SerpentCrownArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.SERPENT_CROWN;
    }

    @Override
    public String getName() {
        return "Serpent Crown";
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Will -1. If you pass,\n" +
                "you may spend 1 Sanity to\n" +
                "gain a random Ally.\n" +
                "\n" +
                "RECKONING: Discard 1 Ally.";
    }
}
