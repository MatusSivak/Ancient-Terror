package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class DunwichHorrorMonster extends AbstractMonsterInfo{

    public DunwichHorrorMonster() {
        super("Dunwich Horror", -1, 3, -2, 3, null);
        setEpic(true);
        setMonsterId(EpicMonsterId.DUNWICH_HORROR);
        setReckoning(true);
        setReckoningText("Roll one die. If the result is less than or equal to COMPLEXITY, one Gate is spawned.");
    }
}
