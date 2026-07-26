package sk.sivak.eldritchhorror.core.eventtype.data.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class DelayedData {
    private InvestigatorId investigatorId;
    private boolean forced;

    public DelayedData(InvestigatorId investigatorId, boolean forced) {
        this.investigatorId = investigatorId;
        this.forced = forced;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }
}
