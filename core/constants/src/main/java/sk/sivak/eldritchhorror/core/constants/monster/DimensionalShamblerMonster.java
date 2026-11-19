package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class DimensionalShamblerMonster extends AbstractMonsterInfo {

    public DimensionalShamblerMonster() {
        super("Dimensional Shambler",
                -1, 2, -1, 0, 2);
        setDamageTestType(Stat.OBSERVATION);
        setSpecialText("Fail Observation: gain Lost in Time and Space.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Dimensional\nShambler";
    }
}
