package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class ZanthuTabletsArtifact extends AbstractArtifactInfo {

    public ZanthuTabletsArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.ZANTHU_TABLETS;
    }

    @Override
    public String getName() {
        return "Zanthu Tablets";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore. If you pass,\n" +
                "you may spend 1 Sanity to gain 2 Spells,\n" +
                "then discard 1 Spell.\n" +
                "\n" +
                "Gain +3 Lore when resolving Spell effects.\n";
    }
}
