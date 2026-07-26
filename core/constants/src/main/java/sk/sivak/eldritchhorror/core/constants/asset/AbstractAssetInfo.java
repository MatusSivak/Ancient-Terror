package sk.sivak.eldritchhorror.core.constants.asset;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author msivak
 */
public abstract class AbstractAssetInfo implements AssetInfo {

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
        AbstractAssetInfo that = (AbstractAssetInfo) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
