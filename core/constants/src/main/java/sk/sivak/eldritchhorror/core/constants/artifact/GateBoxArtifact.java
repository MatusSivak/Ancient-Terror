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
        return "Investigators on your space roll\n" +
                "1 extra die in Other World Encounters.\n" +
                "\n" +
                "Close a Gate there → gain 1 Clue.";
    }
}
