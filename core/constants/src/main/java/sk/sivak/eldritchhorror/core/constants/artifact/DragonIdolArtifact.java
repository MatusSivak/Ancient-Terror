package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class DragonIdolArtifact extends AbstractArtifactInfo {

    public DragonIdolArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.RELIC);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.DRAGON_IDOL;
    }

    @Override
    public String getName() {
        return "Dragon Idol";
    }

    @Override
    public String getDescription() {
        return "ACTION: You lose 1 Sanity\n" +
                "and 1 Monster on your space or\n" +
                "an adjacent space loses 2 Health.\n" +
                "\n" +
                "You may spend 1 Sanity to reroll\n" +
                "any number of dice when resolving\n" +
                "a Strength test during a Combat Encounter.";
    }
}
