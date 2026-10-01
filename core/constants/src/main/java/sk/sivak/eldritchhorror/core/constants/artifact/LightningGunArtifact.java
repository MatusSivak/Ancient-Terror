package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class LightningGunArtifact extends AbstractArtifactInfo {

    public LightningGunArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.LIGHTNING_GUN;
    }

    @Override
    public String getName() {
        return "Lightning Gun";
    }

    @Override
    public String getDescription() {
        return "Combat: +6 Strength.\n" +
                "\n" +
                "ACTION: You and each Monster\n" +
                "on your space lose 1 Health.";
    }
}
