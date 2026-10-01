package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class NecronomiconArtifact extends AbstractArtifactInfo {

    public NecronomiconArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.NECRONOMICON;
    }

    @Override
    public String getName() {
        return "Necronomicon";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore.\n" +
                "Pass: 1 Sanity → gain 2 Spells.";
    }
}
