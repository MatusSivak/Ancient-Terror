package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class DimensionalShamblerMonster extends AbstractMonsterInfo {

    public DimensionalShamblerMonster() {
        super("Dimensional Shambler",
                -1, 2, -1, 0, 2);
        setDamageTestType(Stat.OBSERVATION);
        setSpecialText("If you fail the Observation test, gain a Lost in Time and Space Condition.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Dimensional\nShambler";
    }
}
