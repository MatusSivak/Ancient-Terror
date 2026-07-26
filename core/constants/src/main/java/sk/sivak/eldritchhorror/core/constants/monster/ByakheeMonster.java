package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class ByakheeMonster extends AbstractMonsterInfo {

    public ByakheeMonster() {
        super("Byakhee",
                0, 1, 0, 2, 2);
        setSpecialText("If you defeat this Monster during a Combat Encounter, " +
                "you may lose 1 Sanity and move 3 spaces instead of resolving another encounter.");

    }
}
