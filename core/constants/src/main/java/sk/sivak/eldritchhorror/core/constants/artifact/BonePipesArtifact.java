package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class BonePipesArtifact extends AbstractArtifactInfo {

    public BonePipesArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.BONE_PIPES;
    }

    @Override
    public String getName() {
        return "Bone Pipes";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore.\n" +
                "Pass: 1 Sanity → 1 Monster on or\n" +
                "adjacent to your space loses\n" +
                "2 Health or moves 1 space.";
    }
}
