package sk.sivak.eldritchhorror.core.constants.monster;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

/**
 * @author msivak
 */
public class GnophKehMonster extends AbstractMonsterInfo {

    public GnophKehMonster() {
        super("Gnoph-Keh",
                0, 1, -2, 2, 3);
        setReckoning(true);
        setReckoningText("Each investigator on this space loses 1 Health.");
        setSpawnText("Moves to the Himalayas.");
    }
}
