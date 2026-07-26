package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class BioTableData {
    private InvestigatorId investigatorId;
    private String name;
    private String backgroundBio;

    private BioTableData() {
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public String getName() {
        return name;
    }

    public String getBackgroundBio() {
        return backgroundBio;
    }

    public static class Builder {
        private BioTableData data;

        public Builder() {
            this.data = new BioTableData();
        }

        public Builder investigatorId(InvestigatorId investigatorId) {
            data.investigatorId = investigatorId;
            return this;
        }

        public Builder name(String name) {
            data.name = name;
            return this;
        }

        public BioTableData build() {
            return data;
        }

        public Builder fromInvestigatorBasics(InvestigatorBasics investigatorBasics) {
            data.investigatorId = investigatorBasics.getInvestigatorId();
            data.name = investigatorBasics.getInvestigatorName();
            data.backgroundBio = investigatorBasics.getBio();
            return this;
        }
    }
}
