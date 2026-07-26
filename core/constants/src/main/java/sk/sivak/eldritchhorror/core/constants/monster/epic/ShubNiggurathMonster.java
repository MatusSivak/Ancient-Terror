package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class ShubNiggurathMonster extends AbstractMonsterInfo{

    public ShubNiggurathMonster() {
        super("Shub-Niggurath", -1, 4, -3, 5, null);
        setEpic(true);
        setSpecialText("This Epic Monster cannot lose Health.\n" +
                "Solve three Mysteries first.");
        setMonsterId(EpicMonsterId.SHUB_NIGGURATH);
    }
}
