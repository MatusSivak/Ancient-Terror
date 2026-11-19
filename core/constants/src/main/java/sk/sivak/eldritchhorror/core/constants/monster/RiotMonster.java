package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class RiotMonster extends AbstractMonsterInfo {

    public RiotMonster() {
        super("Riot",
                0, null, -3, 3, 3);
        setSpecialText("Before Strength, you may test Influence (-1). Pass: defeat this Monster.");
    }
}
