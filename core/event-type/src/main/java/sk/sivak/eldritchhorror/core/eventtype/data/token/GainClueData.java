package sk.sivak.eldritchhorror.core.eventtype.data.token;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class GainClueData {
    private InvestigatorId investigatorId;
    private LocationId locationId;
    private final boolean isEncounter;
    private boolean clueGained = false;

    public GainClueData(boolean isEncounter) {
        this.isEncounter = isEncounter;
    }

    public GainClueData(boolean isEncounter, InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
        this.isEncounter = isEncounter;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public void setLocationId(LocationId locationId) {
        this.locationId = locationId;
    }

    public boolean isEncounter() {
        return isEncounter;
    }

    public boolean isClueGained() {
        return clueGained;
    }

    public void setClueGained(boolean clueGained) {
        this.clueGained = clueGained;
    }
}
