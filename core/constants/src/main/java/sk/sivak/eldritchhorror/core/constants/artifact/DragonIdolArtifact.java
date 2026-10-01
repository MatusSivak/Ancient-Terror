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
        return "ACTION: Lose 1 Sanity → 1 Monster on\n" +
                "or adjacent to your space loses 2 Health.\n" +
                "\n" +
                "Combat Strength tests:\n" +
                "1 Sanity → reroll any dice.";
    }
}
