package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class VampireMonster extends AbstractMonsterInfo {

    public VampireMonster() {
        super("Vampire",
                0, 1, -2, 2, 3);
        setSpecialText("- If you fail the Will test, do not resolve the Strength test.\n" +
                "- If you lose Health from the Strength test and this Monster is not defeated, it recovers 2 Health.");
    }
}
