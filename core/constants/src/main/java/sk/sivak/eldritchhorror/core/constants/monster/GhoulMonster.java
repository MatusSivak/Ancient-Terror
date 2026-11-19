package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class GhoulMonster extends AbstractMonsterInfo {

    public GhoulMonster() {
        super("Ghoul",
                1, 1, 0, 2, 1);
        setSpecialText("Lose Health from Strength: gain Paranoia.");
    }
}
