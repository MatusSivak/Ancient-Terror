package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class GrotesqueStatueArtifact extends AbstractArtifactInfo {

    public GrotesqueStatueArtifact() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.GROTESQUE_STATUE;
    }

    @Override
    public String getName() {
        return "Grotesque Statue";
    }

    @Override
    public String getDescription() {
        return "When you gain this card, gain 5 Clues.\n" +
                "\n" +
                "Once per round, you may spend 1 Clue\n" +
                "to prevent all Sanity loss from a single effect.";
    }
}