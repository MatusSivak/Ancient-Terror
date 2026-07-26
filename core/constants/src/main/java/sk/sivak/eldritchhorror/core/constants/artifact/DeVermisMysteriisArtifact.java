package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class DeVermisMysteriisArtifact extends AbstractArtifactInfo {

    public DeVermisMysteriisArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.DE_VERMIS_MYSTERIIS;
    }

    @Override
    public String getName() {
        return "De Vermis Mysteriis";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore. If you pass,\n" +
                "you may spend 1 Sanity to\n" +
                "improve 1 Skill of your choice.";
    }
}
