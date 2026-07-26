package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

/**
 * @author msivak
 */
public class DiscardConditionFromInvestigatorData {

    private InvestigatorId investigatorId;
    private ConditionInfo conditionInfo;

    public DiscardConditionFromInvestigatorData(InvestigatorId investigatorId, ConditionInfo conditionInfo) {
        this.investigatorId = investigatorId;
        this.conditionInfo = conditionInfo;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public ConditionInfo getConditionInfo() {
        return conditionInfo;
    }

    public void setConditionInfo(ConditionInfo conditionInfo) {
        this.conditionInfo = conditionInfo;
    }

    @Override
    public String toString() {
        return "DiscardConditionFromInvestigatorData{" +
                "investigatorId=" + investigatorId +
                ", conditionInfo=" + conditionInfo +
                '}';
    }
}
