package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;

import java.util.Set;

public interface ArtifactInfo extends CardInfo {

    ArtifactId getId();

    Set<AssetTrait> getTraits();

    void disable();

    void enable();
}
