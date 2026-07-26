package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class GhoulMonster extends AbstractMonsterInfo {

    public GhoulMonster() {
        super("Ghoul",
                1, 1, 0, 2, 1);
        setSpecialText("If you lose Health from the Strength test, gain a Paranoia Condition.");
    }
}
