package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class TtkaHalotArtifact extends AbstractArtifactInfo {

    public TtkaHalotArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.TTKA_HALOT;
    }

    @Override
    public String getName() {
        return "T'tka Halot";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore. If you pass,\n" +
                "you may spend 1 Sanity to choose\n" +
                "1 Monster on your space to lose 3 Health.";
    }
}
