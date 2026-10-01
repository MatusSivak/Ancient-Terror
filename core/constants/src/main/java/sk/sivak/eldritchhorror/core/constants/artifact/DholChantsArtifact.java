package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

public class DholChantsArtifact extends AbstractArtifactInfo {

    public DholChantsArtifact() {
        traits.add(AssetTrait.ITEM);
        traits.add(AssetTrait.TOME);
    }

    @Override
    public ArtifactId getId() {
        return ArtifactId.DHOL_CHANTS;
    }

    @Override
    public String getName() {
        return "Dhol Chants";
    }

    @Override
    public String getDescription() {
        return "Combat: you may test Lore.\n" +
                "Pass: 1 Sanity → roll 3 extra dice\n" +
                "on the Combat Strength test.";
    }
}