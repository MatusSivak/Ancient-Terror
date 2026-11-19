package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class CthulhuMonster extends AbstractMonsterInfo{

    public CthulhuMonster() {
        super("Cthulhu", -2, 5, -2, 4, null);
        setEpic(true);
        setSpecialText("Lose Sanity from Will: Cthulhu's power rises!");
        setMonsterId(EpicMonsterId.CTHULHU);
    }
}
