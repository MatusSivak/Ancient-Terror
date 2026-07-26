package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.location.LocationType;

public interface LocationEncounterDeckWrite {

    void init();

    Integer drawCard(String locationType);
}
