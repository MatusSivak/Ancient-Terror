package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class LloigorMonster extends AbstractMonsterInfo {

    public LloigorMonster() {
        super("Lloigor",
                0, 2, -2, 3, 4);
        setReckoning(true);
        setReckoningText("Each investigator here or on an adjacent space loses 1 Health and 1 Sanity.");
    }
}
