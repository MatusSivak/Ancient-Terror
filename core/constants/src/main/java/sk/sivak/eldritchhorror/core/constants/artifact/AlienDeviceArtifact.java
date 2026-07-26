package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class AlienDeviceArtifact extends AbstractArtifactInfo {

    public AlienDeviceArtifact() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.ALIEN_DEVICE;
    }

    @Override
    public String getName() {
        return "Alien Device";
    }

    @Override
    public boolean hasReckoning() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Gain +3 Lore when resolving Spell effects.\n" +
                "\n" +
                "You may spend one Sanity\n" +
                "to reroll any number of dice\n" +
                "when resolving a Lore test\n" +
                "as part of a Spell effect.";
    }
}
