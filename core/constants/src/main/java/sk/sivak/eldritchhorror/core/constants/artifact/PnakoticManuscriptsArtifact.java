package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class PnakoticManuscriptsArtifact extends AbstractArtifactInfo {

    public PnakoticManuscriptsArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.PNAKOTIC_MANUSCRIPTS;
    }

    @Override
    public String getName() {
        return "Pnakotic Manuscripts";
    }

    @Override
    public String getDescription() {
        return "Gain +1 Lore and +1 Will.\n" +
                "\n" +
                "ACTION: If you are on a space\n" +
                "containing a Gate,\n" +
                "gain one Clue.";
    }
}