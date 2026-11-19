package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class VampireMonster extends AbstractMonsterInfo {

    public VampireMonster() {
        super("Vampire",
                0, 1, -2, 2, 3);
        setSpecialText("Fail Will: skip Strength.\nLose Health from Strength: this Monster recovers 2 Health if it survives.");
    }
}
