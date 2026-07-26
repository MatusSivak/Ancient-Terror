package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class ShoggothMonster extends AbstractMonsterInfo {

    public ShoggothMonster() {
        super("Shoggoth",
                0, 3, -2, 2, 4);
        setReckoning(true);
        setReckoningText("This Monster recovers all Health.");
    }
}
