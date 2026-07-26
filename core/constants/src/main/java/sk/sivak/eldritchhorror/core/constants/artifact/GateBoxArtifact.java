package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class GateBoxArtifact extends AbstractArtifactInfo {

    public GateBoxArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.GATE_BOX;
    }

    @Override
    public String getName() {
        return "Gate Box";
    }

    @Override
    public String getDescription() {
        return "Investigators on your space\n" +
                "roll 1 additional die when resolving\n" +
                "tests during Other World Encounters.\n" +
                "\n" +
                "If you close a Gate during\n" +
                "an Other World Encounter, gain 1 Clue.";
    }
}
