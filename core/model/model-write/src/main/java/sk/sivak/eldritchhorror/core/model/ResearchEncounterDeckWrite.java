package sk.sivak.eldritchhorror.core.model;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;

public interface ResearchEncounterDeckWrite {

    void init(AncientOneId ancientOneId);

    Integer drawCard(LocationType locationType);
}
