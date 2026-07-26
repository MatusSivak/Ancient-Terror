package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class PallidMaskArtifact extends AbstractArtifactInfo {

    public PallidMaskArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.PALLID_MASK;
    }

    @Override
    public String getName() {
        return "Pallid Mask";
    }

    @Override
    public String getDescription() {
        return "During the Encounter Phase,\n" +
                "you may choose an encounter as if\n" +
                "there are no Monsters on your space.";
    }
}
