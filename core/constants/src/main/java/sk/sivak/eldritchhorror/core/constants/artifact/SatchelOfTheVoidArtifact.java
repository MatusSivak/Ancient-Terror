package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class SatchelOfTheVoidArtifact extends AbstractArtifactInfo {

    public SatchelOfTheVoidArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.SATCHEL_OF_THE_VOID;
    }

    @Override
    public String getName() {
        return "Satchel of the Void";
    }

    @Override
    public String getDescription() {
        return "ACTION: Look at the top Gate\n" +
                "in the Gate stack.\n" +
                "Gain 1 Clue if that Gate\n" +
                "represents current Omen.\n" +
                "\n"+
                "You cannot gain a\n" +
                "Lost in Time and Space Condition\n" +
                "unless you choose to.";
    }
}
