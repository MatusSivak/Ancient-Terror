package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class DarkYoungMonster extends AbstractMonsterInfo {

    public DarkYoungMonster() {
        super("Dark Young",
                -1, 3, -3, 3, 5);
        setReckoning(true);
        setReckoningText("Roll 1 die. On a 1 or 2, Doom advances.");
    }
}
