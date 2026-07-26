package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.eventlistener.artifact.AbstractArtifactListener;

/**
 * @author msivak
 */
public interface ArtifactListenerProvider {

    AbstractArtifactListener[] getArtifactListeners();

    AbstractArtifactListener<? extends ArtifactInfo> getArtifactListener(ArtifactId artifactId);

    void init();
}
