package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class ManiacMonster extends AbstractMonsterInfo {

    public ManiacMonster() {
        super("Maniac",
                1, 1, -1, 2, 1);
        setSpecialText("- If you fail the Strength test, you may discard 1 Ally Asset instead of losing Health.\n" +
                "- If you defeat this Monster during a Combat Encounter, gain 1 Axe Asset.");
    }
}
