package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class DoppelgangerMonster extends AbstractMonsterInfo{

    public DoppelgangerMonster() {
        super("Doppelganger", -1, 1, -1, 2, 5);
        setReckoning(true);
        setHorrorTestType(Stat.OBSERVATION);
        setEpic(true);
        setReckoningText("Roll 1 die. On a 1 or 2, Doom advances.");
        setMonsterId(EpicMonsterId.DOPPELGANGER);
        setSpecialText("If you fail the Observation test, do not resolve the Strength test.");
    }
}
