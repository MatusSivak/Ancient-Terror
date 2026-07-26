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
        return "You may discard one Spell\n" +
                "to reroll any number of dice\n" +
                "when resolving a test\n" +
                "except when resolving a Spell effect.\n" +
                "\n" +
                "RECKONING: Gain one Spell.";
    }
}
