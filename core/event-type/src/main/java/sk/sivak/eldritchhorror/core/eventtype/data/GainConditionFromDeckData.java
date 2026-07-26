package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

/**
 * @author msivak
 */
public class GainConditionFromDeckData {

    private InvestigatorId investigatorId;
    private ConditionId conditionId;

    public GainConditionFromDeckData(InvestigatorId investigatorId, ConditionId conditionId) {
        this.investigatorId = investigatorId;
        this.conditionId = conditionId;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public ConditionId getConditionId() {
        return conditionId;
    }

    public void setConditionId(ConditionId conditionId) {
        this.conditionId = conditionId;
    }

    @Override
    public String toString() {
        return "GainConditionFromDeckData{" +
                "investigatorId=" + investigatorId +
                ", conditionId=" + conditionId +
                '}';
    }
}
