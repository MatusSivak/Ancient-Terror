package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class MaskOfTheWatcherArtifact extends AbstractArtifactInfo {

    public MaskOfTheWatcherArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.MASK_OF_THE_WATCHER;
    }

    @Override
    public String getName() {
        return "Mask of the Watcher";
    }

    @Override
    public String getDescription() {
        return "Reduce the horror of\n" +
                "Monsters you encounter to 1.\n" +
                "\n" +
                "When you pass a Will test\n" +
                "during a Combat Encounter,\n" +
                "gain 1 Focus.";
    }
}
