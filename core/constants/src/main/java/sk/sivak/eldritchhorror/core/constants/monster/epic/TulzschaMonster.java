package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class TulzschaMonster extends AbstractMonsterInfo{

    public TulzschaMonster() {
        super("Tulzscha", -1, 3, -1, 3, null);
        setReckoning(true);
        setEpic(true);
        setReckoningText("Token spawns on the Green Omen.");
        setMonsterId(EpicMonsterId.TULZSCHA);
    }
}
