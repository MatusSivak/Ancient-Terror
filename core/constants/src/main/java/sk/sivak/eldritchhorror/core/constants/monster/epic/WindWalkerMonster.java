package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class WindWalkerMonster extends AbstractMonsterInfo{

    public WindWalkerMonster() {
        super("Wind Walker", 0, 2, -2, 2, null);
        setEpic(true);
        setMonsterId(EpicMonsterId.WIND_WALKER);
        setSpecialText("Before Will: lose 1 Health and 1 Sanity unless you spend 1 Clue.");
    }
}
