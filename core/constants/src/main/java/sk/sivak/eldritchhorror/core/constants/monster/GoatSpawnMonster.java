package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class GoatSpawnMonster extends AbstractMonsterInfo {

    public GoatSpawnMonster() {
        super("Goat Spawn",
                0, 1, -2, 2, 2);
        setSpecialText("If you defeat this Monster during a Combat Encounter, " +
                "you may gain a Dark Pact Condition to discard 1 Monster from any space.");
    }
}
