package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class YebMonster extends AbstractMonsterInfo{

    public YebMonster() {
        super("Yeb", -1, 3, -2, 3, null);
        setReckoning(false);
        setEpic(true);
        setMonsterId(EpicMonsterId.YEB);
        setSpawnText("Two monsters are spawned on this space.");
    }
}
