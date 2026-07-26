package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;

/**
 * @author msivak
 */
public interface ArtifactListener<T extends ArtifactInfo> {

    T getArtifactInfo();
}
