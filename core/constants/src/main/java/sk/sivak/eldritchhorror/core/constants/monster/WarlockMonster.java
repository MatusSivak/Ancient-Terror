package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class WarlockMonster extends AbstractMonsterInfo {

    public WarlockMonster() {
        super("Warlock",
                0, 1, -1, 1, 1);
        setReckoning(true);
        setReckoningText("Roll 1 die. On a 1 or 2, the nearest investigator gains Cursed.");
        setSpecialText("Fail Will: lose 1 Health.");
    }
}
