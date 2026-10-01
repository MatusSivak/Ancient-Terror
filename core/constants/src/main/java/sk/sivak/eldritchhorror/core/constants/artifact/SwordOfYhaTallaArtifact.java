package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class SwordOfYhaTallaArtifact extends AbstractArtifactInfo {

    public SwordOfYhaTallaArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.WEAPON);
        traits.add(AssetTrait.MAGICAL);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.SWORD_OF_YHA_TALLA;
    }

    @Override
    public String getName() {
        return "Sword of Y'ha-Talla";
    }

    @Override
    public String getDescription() {
        return "Combat: +2 Will and +3 Strength.\n" +
                "\n" +
                "Defeat a Monster in combat →\n" +
                "gain 1 Clue.";
    }
}
