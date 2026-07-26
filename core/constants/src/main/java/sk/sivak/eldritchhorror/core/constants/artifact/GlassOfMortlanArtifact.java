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
        return "Each 6 you roll when resolving\n" +
                "a Spell effect counts as 2 successes.\n" +
                "\n" +
                "You may prevent the loss of 1 Sanity\n" +
                "when resolving your Spell effects.";
    }
}