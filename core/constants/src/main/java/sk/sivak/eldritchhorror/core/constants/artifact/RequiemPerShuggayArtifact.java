package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class RequiemPerShuggayArtifact extends AbstractArtifactInfo {

    public RequiemPerShuggayArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.REQUIEM_PER_SHUGGAY;
    }

    @Override
    public String getName() {
        return "Requiem per Shuggay";
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }

    @Override
    public String getDescription() {
        return "ACTION: Test Lore -1.\n" +
                "Pass: 1 Sanity → discard 1 Monster\n" +
                "on a space with a Gate.\n" +
                "\n" +
                "RECKONING: Roll 1 die. 1 → Doom advances.";
    }
}
