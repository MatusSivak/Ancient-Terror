package sk.sivak.eldritchhorror.core.eventtype.data.token;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class GainFocusData {
    private InvestigatorId investigatorId;

    public GainFocusData() {
    }

    public GainFocusData(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }
}
