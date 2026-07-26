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
        return "When resolving a Combat Encounter,\n" +
                "you may test Lore. If you pass,\n" +
                "you may spend one Sanity\n" +
                "to roll three additional dice\n" +
                "when resolving the Strength test\n" +
                "during that encounter.";
    }
}