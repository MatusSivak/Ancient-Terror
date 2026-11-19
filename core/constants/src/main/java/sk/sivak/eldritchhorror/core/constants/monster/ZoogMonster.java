package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class ZoogMonster extends AbstractMonsterInfo {

    public ZoogMonster() {
        super("Zoog",
                1, 1, -1, 1, 1);
        setDamageTestType(Stat.OBSERVATION);
        setSpecialText("Fail Observation: discard 1 Item possession.");
        setReckoning(true);
        setReckoningText("Move to the nearest investigator. Then that investigator discards 1 Item possession.");
    }
}
