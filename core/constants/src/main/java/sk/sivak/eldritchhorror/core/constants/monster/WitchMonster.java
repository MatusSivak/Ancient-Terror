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
        setReckoningText("Each Cursed investigator loses 1 Health.");
        setSpecialText("Fail Will: gain Cursed.");
    }
}
