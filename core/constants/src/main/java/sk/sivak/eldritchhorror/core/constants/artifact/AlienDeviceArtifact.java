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
        return "Spell effects: +3 Lore.\n" +
                "\n" +
                "Spell effect Lore tests:\n" +
                "1 Sanity → reroll any dice.";
    }
}
