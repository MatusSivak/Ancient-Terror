package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class NightGauntMonster extends AbstractMonsterInfo {

    public NightGauntMonster() {
        super("Night Gaunt",
                0, 2, -2, 1, 2);
        setReckoning(true);
        setReckoningText("Investigator here: move them and this Monster 1 space; they become Delayed. Otherwise, move 2 spaces toward the nearest investigator.");
    }
}
