package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class GlassOfMortlanArtifact extends AbstractArtifactInfo {

    public GlassOfMortlanArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.GLASS_OF_MORTLAN;
    }

    @Override
    public String getName() {
        return "Glass of Mortlan";
    }

    @Override
    public String getDescription() {
        return "Spell effects: each 6 counts\n" +
                "as 2 successes.\n" +
                "\n" +
                "You may prevent 1 Sanity loss\n" +
                "from your Spell effects.";
    }
}