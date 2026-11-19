package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class GhostMonster extends AbstractMonsterInfo {

    public GhostMonster() {
        super("Ghost",
                -1, 2, 0, null, 2);
        setSpecialText("Pass Will: this Monster loses Health equal to your successes.");
    }
}
