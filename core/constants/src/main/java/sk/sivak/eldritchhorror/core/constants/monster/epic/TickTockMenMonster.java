package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class TickTockMenMonster extends AbstractMonsterInfo{

    public TickTockMenMonster() {
        super("Tick Tock Men", 0, null, -2, 3, null);
        setEpic(true);
        setMonsterId(EpicMonsterId.TICK_TOCK_MEN);
        setSpecialText("You cannot spend Clues to reroll dice during this Combat Encounter.");
    }
}
