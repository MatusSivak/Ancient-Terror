package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class DeepOneMonster extends AbstractMonsterInfo {

    public DeepOneMonster() {
        super("Deep One",
                0, 2, -1, 1, 2);
        setReckoning(true);
        setReckoningText("Each investigator on this space loses 1 Sanity.");
    }
}
