package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class WitchMonster extends AbstractMonsterInfo {

    public WitchMonster() {
        super("Witch",
                -1, 0,
                -1, 1, 1);
        setReckoning(true);
        setReckoningText("Each investigator who has a Cursed Condition loses 1 Health.");
        setSpecialText("If you fail the Will test, gain a Cursed Condition.");
    }
}
