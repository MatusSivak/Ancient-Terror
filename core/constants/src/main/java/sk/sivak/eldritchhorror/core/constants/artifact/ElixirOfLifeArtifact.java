package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class ElixirOfLifeArtifact extends AbstractArtifactInfo {

    public ElixirOfLifeArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.ELIXIR);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.ELIXIR_OF_LIFE;
    }

    @Override
    public String getName() {
        return "Elixir of Life";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Will. If you pass,\n" +
                "you may spend 1 Sanity\n" +
                "to recover all Health\n" +
                "and discard all Illness, Injury\n" +
                "and Madness Conditions.";
    }
}
