package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class ZombieMonster extends AbstractMonsterInfo {

    public ZombieMonster() {
        super("Zombie",
                0, 1, -1, 2, 2);
        setReckoning(true);
        setReckoningText("If possible, transforms into the Zombie Horde Epic Monster.");
    }
}
