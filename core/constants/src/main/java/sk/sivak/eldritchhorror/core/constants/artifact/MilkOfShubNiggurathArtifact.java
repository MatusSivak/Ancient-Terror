package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class MilkOfShubNiggurathArtifact extends AbstractArtifactInfo {

    public MilkOfShubNiggurathArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.ELIXIR);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.MILK_OF_SHUB_NIGGURATH;
    }

    @Override
    public String getName() {
        return "Milk of Shub-Niggurath";
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Will. If you pass,\n" +
                "you may spend 1 Sanity\n" +
                "to recover all Health\n" +
                "and improve Strength twice.\n" +
                "Then a Monster ambushes you!";
    }
}
