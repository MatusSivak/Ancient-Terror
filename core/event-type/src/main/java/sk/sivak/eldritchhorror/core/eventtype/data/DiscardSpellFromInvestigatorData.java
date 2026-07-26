package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;

/**
 * @author msivak
 */
public class DiscardSpellFromInvestigatorData {

    private InvestigatorId investigatorId;
    private SpellInfo spellInfo;

    public DiscardSpellFromInvestigatorData(InvestigatorId investigatorId, SpellInfo spellInfo) {
        this.investigatorId = investigatorId;
        this.spellInfo = spellInfo;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public SpellInfo getSpellInfo() {
        return spellInfo;
    }

    public void setSpellInfo(SpellInfo spellInfo) {
        this.spellInfo = spellInfo;
    }

    @Override
    public String toString() {
        return "DiscardSpellFromInvestigatorData [" +
                "investigatorId=" + investigatorId +
                ", spellInfo=" + spellInfo +
                ']';
    }
}
