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
        return "ACTION: Test Lore.\n" +
                "Pass: 1 Sanity → 1 Monster on\n" +
                "your space loses 3 Health.";
    }
}
