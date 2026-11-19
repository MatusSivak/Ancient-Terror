package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class NugMonster extends AbstractMonsterInfo{

    public NugMonster() {
        super("Nug", -1, 2, -2, 4, null);
        setReckoning(true);
        setEpic(true);
        setReckoningText("Spawn 1 Ghoul here.");
        setMonsterId(EpicMonsterId.NUG);
    }
}
