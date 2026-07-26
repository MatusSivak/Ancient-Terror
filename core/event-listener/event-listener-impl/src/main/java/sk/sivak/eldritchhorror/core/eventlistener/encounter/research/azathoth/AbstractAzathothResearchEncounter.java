package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.azathoth;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.AbstractResearchEncounter;

public abstract class AbstractAzathothResearchEncounter extends AbstractResearchEncounter {

    public AbstractAzathothResearchEncounter(int page, LocationType locationType) {
        super(page, locationType, AncientOneId.AZATHOTH);
    }
}
