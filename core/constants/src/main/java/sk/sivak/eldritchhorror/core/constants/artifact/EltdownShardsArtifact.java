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
        return "ACTION: Test Lore. If you pass,\n" +
                "you may spend one Sanity\n" +
                "to discard one Monster\n" +
                "of your choice\n" +
                "with toughness three or less\n" +
                "on any space";
    }
}