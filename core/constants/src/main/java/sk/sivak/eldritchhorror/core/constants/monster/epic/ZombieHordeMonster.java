package sk.sivak.eldritchhorror.core.constants.monster.epic;

import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;

public class ZombieHordeMonster extends AbstractMonsterInfo{

    public ZombieHordeMonster() {
        super("Zombie Horde", 0, 2, -3, 3, 5);
        setReckoning(true);
        setEpic(true);
        setReckoningText("Roll 1 die. On a 1 or 2, advance Doom by 1.");
        setMonsterId(EpicMonsterId.ZOMBIE_HORDE);
        setSpecialText("Lose Health from Strength: discard 1 Ally.");
    }
}
