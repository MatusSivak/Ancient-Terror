package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class DoppelgangerMonster extends AbstractMonsterInfo{

    public DoppelgangerMonster() {
        super("Doppelganger", -1, 1, -1, 2, 5);
        setReckoning(true);
        setHorrorTestType(Stat.OBSERVATION);
        setEpic(true);
        setReckoningText("Roll 1 die. On a 1 or 2, advance Doom by 1.");
        setMonsterId(EpicMonsterId.DOPPELGANGER);
        setSpecialText("Fail Observation: skip Strength.");
    }
}
