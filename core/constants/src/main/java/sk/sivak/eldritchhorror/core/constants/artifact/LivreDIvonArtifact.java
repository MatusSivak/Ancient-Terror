package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class LivreDIvonArtifact extends AbstractArtifactInfo {

    public LivreDIvonArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.LIVRE_D_IVON;
    }

    @Override
    public String getName() {
        return "Livre d'Ivon";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore-1. If you pass,\n" +
                "you may spend one Sanity\n" +
                "to move to any space of your choice.";
    }
}