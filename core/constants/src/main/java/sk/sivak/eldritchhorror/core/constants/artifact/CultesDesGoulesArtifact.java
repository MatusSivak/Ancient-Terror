package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class CultesDesGoulesArtifact extends AbstractArtifactInfo {

    public CultesDesGoulesArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.CULTES_DES_GOULES;
    }

    @Override
    public String getName() {
        return "Cultes des Goules";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore. If you pass,\n" +
                "you may spend 1 Sanity to gain 2 Clues.";
    }
}
