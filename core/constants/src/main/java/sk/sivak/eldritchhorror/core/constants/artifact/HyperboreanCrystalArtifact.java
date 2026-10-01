package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class HyperboreanCrystalArtifact extends AbstractArtifactInfo {

    public HyperboreanCrystalArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.HYPERBOREAN_CRYSTAL;
    }

    @Override
    public String getName() {
        return "Hyperborean Crystal";
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Discard 1 Spell → reroll any dice\n" +
                "on a test (not Spell effects).\n" +
                "\n" +
                "RECKONING: Gain 1 Spell.";
    }
}
