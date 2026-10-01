package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class RubyOfRlyehArtifact extends AbstractArtifactInfo {

    public RubyOfRlyehArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.RUBY_OF_RLYEH;
    }

    @Override
    public String getName() {
        return "Ruby of R'lyeh";
    }

    @Override
    public String getDescription() {
        return "Once per round, in the Action Phase:\n" +
                "1 Sanity → 1 extra action.";
    }
}
