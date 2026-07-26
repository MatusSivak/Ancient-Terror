package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;

import java.util.List;

public class DiscoveredArtifactsRepository implements DiscoveredRepository<ArtifactId>{

    private final DiscoveredRepositoryTemplate<ArtifactId, ArtifactIdHasManyValues> template;

    private static class ArtifactIdHasManyValues implements HasManyValues<ArtifactId> {

        @Override
        public ArtifactId[] values() {
            return ArtifactId.values();
        }
    }

    public DiscoveredArtifactsRepository() {
        template = new DiscoveredRepositoryTemplate<>(new ArtifactIdHasManyValues());
        template.setKeyName("discoveredArtifacts");
    }


    @Override
    public void saveDiscovered(List<ArtifactId> discoveredArtifacts) {
        template.saveData(discoveredArtifacts);
    }

    @Override
    public List<ArtifactId> getDiscovered() {
        return template.getDiscovered();
    }
}
