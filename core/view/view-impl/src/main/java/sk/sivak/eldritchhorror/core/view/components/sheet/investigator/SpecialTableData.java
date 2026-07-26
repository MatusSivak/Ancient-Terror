package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;

public class SpecialTableData {
    private String action;
    private String ability;

    private SpecialTableData() {
    }

    public String getAction() {
        return action;
    }

    public String getAbility() {
        return ability;
    }

    public static class Builder {
        private SpecialTableData data;

        public Builder() {
            this.data = new SpecialTableData();
        }

        public Builder action(String action) {
            data.action = action;
            return this;
        }

        public Builder ability(String ability) {
            data.ability = ability;
            return this;
        }

        public SpecialTableData build() {
            return data;
        }

        public Builder fromInvestigatorBasics(InvestigatorBasics investigatorBasics) {
            data.action = investigatorBasics.getActionText();
            data.ability = investigatorBasics.getAbilityText();
            return this;
        }
    }
}
