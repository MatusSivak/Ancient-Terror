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
        return "ACTION: Test Lore.\n" +
                "Pass: 1 Sanity → gain 2 Spells,\n" +
                "then discard 1 Spell.\n" +
                "\n" +
                "Spell effects: +3 Lore.";
    }
}
