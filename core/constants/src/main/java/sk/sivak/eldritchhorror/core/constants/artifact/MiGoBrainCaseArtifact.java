package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class MiGoBrainCaseArtifact extends AbstractArtifactInfo {

    public MiGoBrainCaseArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.TEAMWORK);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.MI_GO_BRAIN_CASE;
    }

    @Override
    public String getName() {
        return "Mi-go Brain Case";
    }

    @Override
    public String getDescription() {
        return "ACTION: You and another investigator\n" +
                "may trade possessions. They may\n" +
                "then swap spaces with you.";
    }
}