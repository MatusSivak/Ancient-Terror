package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class KhopeshOfTheAbyssArtifact extends AbstractArtifactInfo {

    public KhopeshOfTheAbyssArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.MAGICAL);
        traits.add(AssetTrait.RELIC);
        traits.add(AssetTrait.WEAPON);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.KHOPESH_OF_THE_ABYSS;
    }

    @Override
    public String getName() {
        return "Khopesh of the Abyss";
    }

    @Override
    public String getDescription() {
        return "Gain +5 Strength during Combat Encounters.\n" +
                "\n" +
                "Once per round, when you defeat\n" +
                "a Monster during a Combat Encounter,\n" +
                "you may move to the nearest space\n" +
                "containing a Monster.";
    }
}
