package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class SandDwellerMonster extends AbstractMonsterInfo {

    public SandDwellerMonster() {
        super("Sand Dweller",
                -2, 1, 0, 2, 2);
        setHorrorTestType(Stat.OBSERVATION);
        setSpecialText("Fail Observation: discard 1 Ally Asset.");
    }

    @Override
    public String getNameInSelectComponent() {
        return "Sand\nDweller";
    }
}
