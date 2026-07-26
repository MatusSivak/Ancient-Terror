package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class ZombieHordeMonster extends AbstractMonsterInfo{

    public ZombieHordeMonster() {
        super("Zombie Horde", 0, 2, -3, 3, 5);
        setReckoning(true);
        setEpic(true);
        setReckoningText("Roll 1 die. On a 1 or 2, Doom advances.");
        setMonsterId(EpicMonsterId.ZOMBIE_HORDE);
        setSpecialText("If you lose Health from the Strength test, discard one Ally.");
    }
}
