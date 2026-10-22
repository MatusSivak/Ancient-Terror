package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;

public class RatThingMonster extends AbstractMonsterInfo {

    public RatThingMonster() {
        super("Rat-thing",
                -2, 0, -1, 1, 1);
        setHorrorTestType(Stat.OBSERVATION);
        setSpecialText("If you fail the Observation test, discard 1 Item possession.");
    }
}
