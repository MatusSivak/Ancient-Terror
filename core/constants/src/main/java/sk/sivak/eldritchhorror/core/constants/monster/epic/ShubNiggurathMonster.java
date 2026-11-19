package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class ShubNiggurathMonster extends AbstractMonsterInfo{

    public ShubNiggurathMonster() {
        super("Shub-Niggurath", -1, 4, -3, 5, null);
        setEpic(true);
        setSpecialText("Cannot lose Health until 3 Mysteries are solved.");
        setMonsterId(EpicMonsterId.SHUB_NIGGURATH);
    }
}
