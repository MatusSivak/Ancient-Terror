package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class GhostMonster extends AbstractMonsterInfo {

    public GhostMonster() {
        super("Ghost",
                -1, 2, 0, null, 2);
        setSpecialText("If you pass the Will test, this Monster loses Health equal to the test result.");
    }
}
