package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class ZoogMonster extends AbstractMonsterInfo {

    public ZoogMonster() {
        super("Zoog",
                1, 1, -1, 1, 1);
        setDamageTestType(Stat.OBSERVATION);
        setSpecialText("If you fail the Observation test, discard 1 Item possession.");
        setReckoning(true);
        setReckoningText("Move this Monster to the nearest investigator. Then that investigator discards 1 Item possession.");
    }
}
