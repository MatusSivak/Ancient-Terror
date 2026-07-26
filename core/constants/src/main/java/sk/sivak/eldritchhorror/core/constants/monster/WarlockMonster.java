package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class WarlockMonster extends AbstractMonsterInfo {

    public WarlockMonster() {
        super("Warlock",
                0, 1, -1, 1, 1);
        setReckoning(true);
        setReckoningText("Roll 1 die. On a 1 or 2, the nearest investigator gains a Cursed Condition.");
        setSpecialText("If you fail the Will test, lose 1 Health.");
    }
}
