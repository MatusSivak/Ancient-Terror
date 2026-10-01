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
        return "When gained → gain 5 Clues.\n" +
                "\n" +
                "Once per round: 1 Clue → prevent\n" +
                "all Sanity loss from 1 effect.";
    }
}