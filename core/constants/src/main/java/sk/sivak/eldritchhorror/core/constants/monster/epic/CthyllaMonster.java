package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class CthyllaMonster extends AbstractMonsterInfo{

    public CthyllaMonster() {
        super("Cthylla", -1, 4, -2, 2, null);
        setReckoning(true);
        setEpic(true);
        setReckoningText("A Deep One Monster ambushes the nearest investigator.");
        setMonsterId(EpicMonsterId.CTHYLLA);
    }
}
