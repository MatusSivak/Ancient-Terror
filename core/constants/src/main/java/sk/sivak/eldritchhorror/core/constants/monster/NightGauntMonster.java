package sk.sivak.eldritchhorror.core.constants.monster;

/**
 * @author msivak
 */
public class NightGauntMonster extends AbstractMonsterInfo {

    public NightGauntMonster() {
        super("Night Gaunt",
                0, 2, -2, 1, 2);
        setReckoning(true);
        setReckoningText("If there is an investigator on this space, " +
                "move him and this Monster 1 space and he becomes Delayed. " +
                "Otherwise, move this Monster 2 spaces toward the nearest investigator.");
    }
}
