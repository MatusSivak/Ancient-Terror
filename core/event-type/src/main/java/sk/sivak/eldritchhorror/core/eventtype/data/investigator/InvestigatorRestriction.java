package sk.sivak.eldritchhorror.core.eventtype.data.investigator;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class InvestigatorRestriction {
    private String title = "Select Investigator:";
    private List<InvestigatorId> allowedInvestigators;
    private List<InvestigatorId> disabledInvestigators;
    private boolean canBeDelayedOrDetained;

    public InvestigatorRestriction(boolean canBeDelayedOrDetained) {
        this.canBeDelayedOrDetained = canBeDelayedOrDetained;
    }

    public boolean canBeDelayedOrDetained() {
        return canBeDelayedOrDetained;
    }

    public InvestigatorId[] getAllowedInvestigators() {
        if (allowedInvestigators == null) {
            return new InvestigatorId[]{};
        }
        return allowedInvestigators.toArray(new InvestigatorId[allowedInvestigators.size()]);
    }

    public InvestigatorId[] getDisabledInvestigators() {
        if (disabledInvestigators == null) {
            return new InvestigatorId[]{};
        }
        return disabledInvestigators.toArray(new InvestigatorId[disabledInvestigators.size()]);
    }

    public InvestigatorRestriction addAllowedInvestigator(InvestigatorId investigatorId) {
        if (allowedInvestigators == null) {
            allowedInvestigators = new LinkedList<>();
        }
        allowedInvestigators.add(investigatorId);
        return this;
    }

    public InvestigatorRestriction addAllowedInvestigators(Collection<InvestigatorId> investigatorIds) {
        if (allowedInvestigators == null) {
            allowedInvestigators = new LinkedList<>();
        }
        allowedInvestigators.addAll(investigatorIds);
        return this;
    }

    public InvestigatorRestriction addDisabledInvestigator(InvestigatorId investigatorId) {
        if (disabledInvestigators == null) {
            disabledInvestigators = new LinkedList<>();
        }
        disabledInvestigators.add(investigatorId);
        return this;
    }

    @Override
    public String toString() {
        return "InvestigatorRestriction{" +
                "allowedInvestigators=" + allowedInvestigators +
                ", disabledInvestigators=" + disabledInvestigators +
                '}';
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
