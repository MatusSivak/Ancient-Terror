package sk.sivak.eldritchhorror.core.eventtype.data;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;

/**
 * @author msivak
 */
public class GainSpellFromDeckData {

    private InvestigatorId investigatorId;
    private SpellId spellId;

    public GainSpellFromDeckData(InvestigatorId investigatorId, SpellId spellId) {
        this.investigatorId = investigatorId;
        this.spellId = spellId;
    }

    public InvestigatorId getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(InvestigatorId investigatorId) {
        this.investigatorId = investigatorId;
    }

    public SpellId getSpellId() {
        return spellId;
    }

    public void setSpellId(SpellId spellId) {
        this.spellId = spellId;
    }

    @Override
    public String toString() {
        return "GainSpellFromDeckData{" +
                "investigatorId=" + investigatorId +
                ", spellId=" + spellId +
                '}';
    }
}
