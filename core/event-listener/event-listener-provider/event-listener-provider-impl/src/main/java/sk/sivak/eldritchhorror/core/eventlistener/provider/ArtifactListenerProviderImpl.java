package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactInfo;
import sk.sivak.eldritchhorror.core.eventlistener.artifact.AbstractArtifactListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArtifactListenerProviderImpl implements ArtifactListenerProvider {
    private Map<ArtifactId, AbstractArtifactListener<? extends ArtifactInfo>> listenersMap;

    @Override
    public void init() {
        listenersMap = new HashMap<>();
    }

    @Override
    public AbstractArtifactListener[] getArtifactListeners() {
        ArrayList<AbstractArtifactListener> allListeners = new ArrayList<>();
        for (ArtifactId artifactId : ArtifactId.values()) {
            allListeners.add(getArtifactListener(artifactId));
        }
        return allListeners.toArray(new AbstractArtifactListener[allListeners.size()]);
    }

    @Override
    public AbstractArtifactListener<? extends ArtifactInfo> getArtifactListener(ArtifactId artifactId) {
        AbstractArtifactListener<?> listener = listenersMap.get(artifactId);
        if (listener == null) {
            try {
                listener = (AbstractArtifactListener<?>) Class.forName(artifactId.getArtifactListenerClassName()).newInstance();
                listenersMap.put(artifactId, listener);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return listener;
    }
}
