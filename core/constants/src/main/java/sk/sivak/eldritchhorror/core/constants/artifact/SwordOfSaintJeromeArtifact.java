package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class SwordOfSaintJeromeArtifact extends AbstractArtifactInfo {

    public SwordOfSaintJeromeArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.SWORD_OF_SAINT_JEROME;
    }

    @Override
    public String getName() {
        return "Sword of Saint Jerome";
    }

    @Override
    public String getDescription() {
        return "Gain +2 Will and +5 Strength\n" +
                "when resolving Combat Encounters.\n" +
                "\n" +
                "If you defeat a Monster\n" +
                "during a Combat Encounter,\n" +
                "recover 1 Sanity.";
    }
}
