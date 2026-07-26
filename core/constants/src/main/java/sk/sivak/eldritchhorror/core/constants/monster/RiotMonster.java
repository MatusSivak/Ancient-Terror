package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class RiotMonster extends AbstractMonsterInfo {

    public RiotMonster() {
        super("Riot",
                0, null, -3, 3, 3);
        setSpecialText("Before resolving the Strength test, " +
                "you may attempt to disperse the mob (Test Influence -1). If you pass, defeat this Monster.");
    }
}
