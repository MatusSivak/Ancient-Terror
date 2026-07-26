package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class GugMonster extends AbstractMonsterInfo {

    public GugMonster() {
        super("Gug",
                0, 2, -2, 3, 4);
        setSpecialText("If you defeat this Monster during a Combat Encounter, " +
                "you do not resolve an additional Encounter.");
    }
}
