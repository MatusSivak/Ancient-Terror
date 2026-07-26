package sk.sivak.eldritchhorror.core.constants;

import java8.features.function.Supplier;
import rx.Producer;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.List;

/**
 * @author msivak
 */
public interface MysteryCardInfo {

    MysteryCardId getMysteryCardId();

    AncientOneId getAncientOneId();

    String getName();

    String getFlavorText();

    List<LocationId> getPinLocations();

    String getMysteryText();

    Integer getMysteryComplexity();

    Integer getProgress();

    void setProgressSupplier(Supplier<Integer> progressSupplier);

    void setPinLocationsSupplier(Supplier<List<LocationId>> pinLocationsSupplier);
}
