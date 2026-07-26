package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class CursedSphereArtifact extends AbstractArtifactInfo {

    public CursedSphereArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.CURSED_SPHERE;
    }

    @Override
    public String getName() {
        return "Cursed Sphere";
    }

    @Override
    public boolean hasReckoning() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Gain +2 to all skills.\n" +
                "\n" +
                "RECKONING: Roll 1 die.\n" +
                "On a 1 or 2, gain a Cursed Condition.";
    }
}
