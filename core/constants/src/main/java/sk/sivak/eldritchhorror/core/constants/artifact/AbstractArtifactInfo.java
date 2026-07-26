package sk.sivak.eldritchhorror.core.constants.artifact;

import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author msivak
 */
public abstract class AbstractArtifactInfo implements ArtifactInfo {

    protected Set<AssetTrait> traits = new HashSet<>();
    private boolean disabled = false;

    public Set<AssetTrait> getTraits() {
        return traits;
    }

    @Override
    public boolean hasReckoning() {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void disable() {
        disabled = true;
    }

    public void enable() {
        disabled = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractArtifactInfo that = (AbstractArtifactInfo) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
