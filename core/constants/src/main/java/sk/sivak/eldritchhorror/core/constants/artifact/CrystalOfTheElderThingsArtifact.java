package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class CrystalOfTheElderThingsArtifact extends AbstractArtifactInfo {

    public CrystalOfTheElderThingsArtifact() {
        traits.add(AssetTrait.ITEM);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.CRYSTAL_OF_THE_ELDER_THINGS;
    }

    @Override
    public String getName() {
        return "Elder Things Crystal";
    }

    @Override
    public String getDescription() {
        return "Mythos card effects cannot\n" +
                "make you lose Health or Sanity.";
    }
}