package sk.sivak.eldritchhorror.core.eventtype.data.spell;

import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

public class ResolvedSpellData {
    private SpellInfo spellInfo;
    private TestData testData;

    public ResolvedSpellData(SpellInfo spellInfo, TestData testData) {
        this.spellInfo = spellInfo;
        this.testData = testData;
    }

    public SpellInfo getSpellInfo() {
        return spellInfo;
    }

    public TestData getTestData() {
        return testData;
    }

    @Override
    public String toString() {
        return "ResolvedSpellData [" +
                "spellInfo=" + spellInfo +
                ", testData=" + testData +
                ']';
    }
}
