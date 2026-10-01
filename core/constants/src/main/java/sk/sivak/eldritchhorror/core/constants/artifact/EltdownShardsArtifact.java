package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class EltdownShardsArtifact extends AbstractArtifactInfo {

    public EltdownShardsArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.ELTDOWN_SHARDS;
    }

    @Override
    public String getName() {
        return "Eltdown Shards";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore.\n" +
                "Pass: 1 Sanity → discard 1 Monster\n" +
                "on any space with toughness 3 or less.";
    }
}